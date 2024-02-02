/* global converters, curve25519, CryptoJS */

function signBytes(message, secretPhrase) {
    if (!secretPhrase) {
        throw {"message": $.t("error_encryption_passphrase_required"), "errorCode": 1};
    }
    var messageBytes = converters.hexStringToByteArray(message);
    var secretPhraseBytes = converters.hexStringToByteArray(converters.stringToHexString(secretPhrase));

    var digest = simpleHash(secretPhraseBytes);
    var s = curve25519.keygen(digest).s;
    var m = simpleHash(messageBytes);
    var x = simpleHash(m, s);
    var y = curve25519.keygen(x).p;
    var h = simpleHash(m, y);
    var v = curve25519.sign(h, x, s);
    var signature = converters.byteArrayToHexString(v.concat(h));

    var payload = message.substr(0, 192) + signature + message.substr(320);

    return payload;
}


function simpleHash(b1, b2) {
    var sha256 = CryptoJS.algo.SHA256.create();
    sha256.update(converters.byteArrayToWordArray(b1));
    if (b2) {
        sha256.update(converters.byteArrayToWordArray(b2));
    }
    var hash = sha256.finalize();
    return converters.wordArrayToByteArrayImpl(hash, false);
}

function gAIbyteArrayToBigInteger(byteArray) {
    var value = new BigInteger("0", 10);
    var temp1, temp2;
    for (var i = byteArray.length - 1; i >= 0; i--) {
        temp1 = value.multiply(new BigInteger("256", 10));
        temp2 = temp1.add(new BigInteger(byteArray[i].toString(10), 10));
        value = temp2;
    }

    return value;
}


function getPublicKeyPrizm(secretPhrase) {
    var secretPhraseBytes = converters.hexStringToByteArray(converters.stringToHexString(secretPhrase));
    var digest = simpleHash(secretPhraseBytes);
    return converters.byteArrayToHexString(curve25519.keygen(digest).p);
}

function getPrivateKey(secretPhrase) {
    var bytes = simpleHash(converters.stringToByteArray(secretPhrase));
    return converters.shortArrayToHexString(curve25519_clamp(converters.byteArrayToShortArray(bytes)));
    
}

function getAccountId(publicKey) {
    try {
        var hex = converters.hexStringToByteArray(publicKey);
        var account = simpleHash(hex);
        account = converters.byteArrayToHexString(account);
        var slice = (converters.hexStringToByteArray(account)).slice(0, 8);
        var accountId = gAIbyteArrayToBigInteger(slice).toString();
        return accountId;
    } catch (err) {
        return null;
    }

}

function getRSaddressPrizm(accountId) {
    var address = new PrizmAddress();
    if (address.set(accountId)) {
        return address.toString();
    } else {
        return "no address";
    }
}

function getIDByRSaddressPrizm(accountAddress) {
    var address = new PrizmAddress();
    if (address.set(accountAddress)) {
        return address.account_id();
    } else {
        return "no address";
    }
}

function getSharedSecret(key1, key2) {
    return converters.shortArrayToByteArray(curve25519_(converters.byteArrayToShortArray(key1), converters.byteArrayToShortArray(key2), null));
}

function aesDecrypt(ivCiphertext, options) {
    if (ivCiphertext.length < 16 || ivCiphertext.length % 16 !== 0) {
        throw {
            name: "invalid ciphertext"
        };
    }

    var iv = converters.byteArrayToWordArray(ivCiphertext.slice(0, 16));
    var ciphertext = converters.byteArrayToWordArray(ivCiphertext.slice(16));
    var sharedKey;
    if (!options.sharedKey) {
        sharedKey = getSharedSecret(options.privateKey, options.publicKey);
    } else {
        sharedKey = options.sharedKey.slice(0); //clone
    }

    var key;
    if (options.nonce) {
        for (var i = 0; i < 32; i++) {
            sharedKey[i] ^= options.nonce[i];
        }
        key = CryptoJS.SHA256(converters.byteArrayToWordArray(sharedKey));
    } else {
        key = converters.byteArrayToWordArray(sharedKey);
    }

    var encrypted = CryptoJS.lib.CipherParams.create({
        ciphertext: ciphertext,
        iv: iv,
        key: key
    });

    var decrypted = CryptoJS.AES.decrypt(encrypted, key, {
        iv: iv
    });

    return converters.wordArrayToByteArray(decrypted);
}

function aesDecryptSymmetric(ivCiphertext, key) {
    return aesDecrypt(ivCiphertext, {"sharedKey":key});
}

function aesEncrypt(plaintext, options) {
    if (!window.crypto && !window.msCrypto) {
        throw {
            "errorCode": -1,
            "message": "error_encryption_browser_support"
        };
    }

    // CryptoJS likes WordArray parameters
    var text = converters.byteArrayToWordArray(plaintext);
    var sharedKey;
    if (!options.sharedKey) {
        sharedKey = getSharedSecret(options.privateKey, options.publicKey);
    } else {
        sharedKey = options.sharedKey.slice(0); //clone
    }

    for (var i = 0; i < 32; i++) {
        sharedKey[i] ^= options.nonce[i];
    }

    var key = CryptoJS.SHA256(converters.byteArrayToWordArray(sharedKey));

    var tmp = new Uint8Array(16);

    if (window.crypto) {
        window.crypto.getRandomValues(tmp);
    } else {
        window.msCrypto.getRandomValues(tmp);
    }

    var iv = converters.byteArrayToWordArray(tmp);
    var encrypted = CryptoJS.AES.encrypt(text, key, {
        iv: iv
    });

    var ivOut = converters.wordArrayToByteArray(encrypted.iv);

    var ciphertextOut = converters.wordArrayToByteArray(encrypted.ciphertext);

    return ivOut.concat(ciphertextOut);
}

function aesEncryptSymmetric(plaintext, key) {
    var nonce = new Uint8Array(32);
    if (window.crypto) {
        window.crypto.getRandomValues(nonce);
    } else {
        window.msCrypto.getRandomValues(nonce);
    }
    return aesEncrypt(plaintext, {sharedKey:key,nonce:nonce});
}


function decryptData(data, options) {
    if (!options.sharedKey) {
        options.sharedKey = getSharedSecret(options.privateKey, options.publicKey);
    }

    var compressedPlaintext = aesDecrypt(data, options);
    var binData = new Uint8Array(compressedPlaintext);
    return converters.byteArrayToString(pako.inflate(binData));
}


function encryptData(plaintext, options) {
    if (!window.crypto && !window.msCrypto) {
        throw {
            "errorCode": -1,
            "message": "error_encryption_browser_support"
        };
    }

    if (!options.sharedKey) {
        options.sharedKey = getSharedSecret(options.privateKey, options.publicKey);
    }

    var compressedPlaintext = pako.gzip(new Uint8Array(plaintext));
    options.nonce = new Uint8Array(32);
    if (window.crypto) {
//noinspection JSUnresolvedFunction
        window.crypto.getRandomValues(options.nonce);
    } else {
//noinspection JSUnresolvedFunction
        window.msCrypto.getRandomValues(options.nonce);
    }

    var data = aesEncrypt(compressedPlaintext, options);
    return {
        "nonce": options.nonce,
        "data": data
    };
}

function decryptNote(message, options, secretPhrase) {
    try {
        if (!options.sharedKey) {
            if (!options.privateKey) {
                options.privateKey = converters.hexStringToByteArray(getPrivateKey(secretPhrase));
            }

            if (!options.publicKey) {
                if (!options.account) {
                    throw {
                        "message": $.t("error_account_id_not_specified"),
                        "errorCode": 2
                    };
                }

                options.publicKey = converters.hexStringToByteArray(getPublicKeyPrizm(secretPhrase));
            }
        }

        options.nonce = converters.hexStringToByteArray(options.nonce);

        return decryptData(converters.hexStringToByteArray(message), options);
    } catch (err) {
        if (err.errorCode && err.errorCode < 3) {
            throw err;
        } else {
            throw {
                "message": "error_message_decryption",
                "errorCode": 3
            };
        }
    } 
}

function encryptNote(message, options, secretPhrase) {
    try {
        if (!options.sharedKey) {
            if (!options.privateKey) {
                if (!secretPhrase) {
                        throw {
                            "message": "error_encryption_passphrase_required",
                            "errorCode": 1
                        };
                }

                options.privateKey = converters.hexStringToByteArray(getPrivateKey(secretPhrase));
            }

            if (!options.publicKey) {
                if (!options.account) {
                    throw {
                        "message": "error_account_id_not_specified",
                        "errorCode": 2
                    };
                }

                try {
                    options.publicKey = converters.hexStringToByteArray(getPublicKeyPrizm(options.account, true));
                } catch (err) {
                    var pzmAddress = new PrizmAddress();

                    if (!pzmAddress.set(options.account)) {
                        throw {
                            "message": "error_invalid_account_id",
                            "errorCode": 3
                        };
                    } else {
                        throw {
                            "message": "error_public_key_not_specified",
                            "errorCode": 4
                        };
                    }
                }
            } else if (typeof options.publicKey == "string") {
                options.publicKey = converters.hexStringToByteArray(options.publicKey);
            }
        }

        var encrypted = encryptData(converters.stringToByteArray(message), options);

        return {
            "message": converters.byteArrayToHexString(encrypted.data),
            "nonce": converters.byteArrayToHexString(encrypted.nonce)
        };
    } catch (err) {
        return err;
    }
}
;

function decMessage(encrypted, nonce, publicKey, password) {
    try {
        var data = decryptNote(encrypted, {
            "nonce": nonce,
            "publicKey": converters.hexStringToByteArray(publicKey)
        }, password);
        return data;
    } catch (err) {
        return "error";
    }
}

function encMessage(message, account, publicKey, password) {
    try {
        var data = encryptNote(message, {
            "account": account,
            "publicKey": converters.hexStringToByteArray(publicKey)
        }, password);
        return data["message"] + ":" + data["nonce"];
    } catch (err) {
        return "error";
    }
}

function qrCodemaster(id, message) {
    document.getElementById(id).innerHTML = "";
    var qrcode = new QRCode(document.getElementById(id), {
        width: 600,
        height: 600,
        colorLight: "#f5f5f5"
    });
    qrcode.makeCode(message);
}

function itemClick(itemId) {
    var elem = document.getElementById('value'+itemId);
    elem.classList.toggle('open');
    elem = document.getElementById('arrow'+itemId);
    elem.classList.toggle('up');
    elem.classList.toggle('down');
    elem = document.getElementById(itemId);
    elem.classList.toggle('open');
}


function copyTextToClipboard(text, elem, repeat) {
      var dummyButton = document.getElementById('copy-button');
      var dummy = document.getElementById('copy-input');
      dummy.value = text;
      var elem2 = elem;
      elem2.classList.remove("btn-copy");
      elem2.classList.add("btn-copy-ok");
      setTimeout(function(){
        elem2.classList.remove("btn-copy-ok");
        elem2.classList.add("btn-copy");
      }, 500);
      dummyButton.click();
      dummy.blur();
}

var accountToSend = null;
var scanResult = null;
var scanning = false;
QrScanner.WORKER_PATH = './js/qr-scanner-worker.min.js';
var qrScanner = null;

function getAccountToSend() {
    return accountToSend;
}

function setAccountToSend(account) {
    accountToSend = account;
}

function stopScanner() {
    scanning = false;
    qrScanner.stop();
    qrScanner.destroy();
    qrScanner = null;
    document.getElementById('prizm-gaze-overlay').classList.remove('open');
}

function setScanResult(result) {
    stopScanner();
    scanResult = result;
}

function startScanner() {
    scanning = true;
    document.getElementById('prizm-gaze-overlay').classList.add('open');
    qrScanner = new QrScanner(document.getElementById('scanner-view'), result => setScanResult(result));
    qrScanner.start();
    qrScanner.hasFlash().then(flash => {if(!flash){document.getElementById('flashlight-button').remove();}});                    
}

function isScanning() {
    return scanning;
}



function getScanResult() {
    var val=scanResult;
    scanResult=null;
    return val;
}

function hasCamera() {
    return QrScanner.hasCamera();
}

function hasFlashlight() {
    return QrScanner.hasFlash();
}

function toggleFlashlight() {
    if (qrScanner != null) {
        qrScanner.toggleFlash();
    }
}

var developmentMode = null;

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    var items = location.search.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    }
    return result;
}

setTimeout(function(){
    QrScanner.hasCamera().then(camera=>{if(!camera){
        if (developmentMode) return;
        document.getElementById('prizm-gaze-overlay').remove();
        document.getElementById('header-button-scan').remove();
    } else {
        if (findGetParameter('client')) {
                document.getElementById('prizm-gaze-overlay').remove();
                document.getElementById('header-button-scan').remove();
        }   
    }});
}, 3000);


function printLegacy(title,elementId) {
    var printWindow = window.open('', 'PRINT', 'height=400,width=600');
    printWindow.document.write('<html><head><title>'+title+'</title>');
    printWindow.document.write('</head><body >');
    printWindow.document.write('<h1>'+title+'</h1>');
    printWindow.document.write(document.getElementById(elementId).innerHTML);
    printWindow.document.write('</body></html>');
    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
    printWindow.close();
    return true;
}

function printElement(title,elementId) {
    var source = document.getElementById(elementId);
    var printer = document.getElementById('printme');
    printer.innerHTML = source.innerHTML;
    var titleBackup = window.document.title;
    window.document.title = title;
    window.print();
    window.document.title = titleBackup;
}
