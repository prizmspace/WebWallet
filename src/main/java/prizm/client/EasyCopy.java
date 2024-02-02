package prizm.client;

import com.google.gwt.user.client.ui.HTML;
import prizm.client.service.Epoch;

public class EasyCopy {
    public static HTML createCopyButtonGWT(String textToCopy) {
        String html = "<button title=\"Copy\" type=\"button\" class=\"btn-copy\" onclick=\"copyTextToClipboard(\'"+textToCopy+"\', this, false)\">" +
                "<i class=\"fa fa-copy\"></i><i class=\"fa fa-check\"></i></button>";
        return new HTML(html);
    }

    public static String createCopyButtonHTML(String textToCopy) {
        if (textToCopy == null || textToCopy.isEmpty())
            return "";
        if (textToCopy.equalsIgnoreCase(Epoch.GENESIS))
            return "";
        return  "<button title=\"Copy\" type=\"button\" class=\"btn-copy\" onclick=\"copyTextToClipboard(\'"+textToCopy+"\', this, false)\">" +
                "<i class=\"fa fa-copy\"></i><i class=\"fa fa-check\"></i></button>";
    }
}
