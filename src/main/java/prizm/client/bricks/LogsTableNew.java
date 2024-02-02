package prizm.client.bricks;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.PanelType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import prizm.client.EasyCopy;
import prizm.client.Prizm;
import prizm.client.pojo.Transaction;
import prizm.client.service.DataUtils;
import prizm.client.service.Epoch;

public class LogsTableNew extends Panel {

    private CellTable<Transaction> table = new CellTable<Transaction>(100);
    ShortPagination<Transaction> pagination;

    public ShortPagination<Transaction> getPagination() {
        return pagination;
    }

    public CellTable<Transaction> getTable() {
        return table;
    }
    private Date selectedDate = new Date();
    private Date lastFetchedDate = new Date(0);

    public void clearData() {
        getPagination().replace(new ArrayList<Transaction>());
    }

    private org.gwtbootstrap3.client.ui.Column dateColumn = new org.gwtbootstrap3.client.ui.Column("SM_12 LG_12 XS_12 MD_12");
    private org.gwtbootstrap3.client.ui.Column titleColumn = new org.gwtbootstrap3.client.ui.Column("SM_4 LG_4 XS_4 MD_4");
    private org.gwtbootstrap3.client.ui.Column pagerColumn = new org.gwtbootstrap3.client.ui.Column("SM_8 LG_8 XS_8 MD_8");

    public static String feeFormat(double value) {
        return DataUtils.formatNumber(value).replaceAll("-", "");
    }

    private boolean showTransactionNumbers = true;

    private NoSelectionModel<Transaction> selectionModel = new NoSelectionModel<Transaction>();

    private SelectionChangeEvent.Handler selectionHandler = new SelectionChangeEvent.Handler() {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {

        }
    };

    public void unSelectAll() {
//        selectionModel.clear();
    }

    public void update(List<Transaction> transactions) {
        pagination.replace(Prizm.checkSavedTransaction(transactions));
        pagination.insert(Prizm.getSavedTransaction());
    }

    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("HH:mm / dd.MM.yyyy");

    private static void parseTransaction(SafeHtmlBuilder builder, Transaction transaction) {

    }
    
    static int id = 1;

    private native String getAccountId(String accountRs) /*-{
        return $wnd.getIDByRSaddressPrizm(accountRs);
    }-*/;
    
    public LogsTableNew() {
        this.showTransactionNumbers = showTransactionNumbers;
        PanelHeader header = new PanelHeader();
        PanelBody body = new PanelBody();
        add(header);
        add(body);
        setType(PanelType.DEFAULT);

        Heading titleHead = new Heading(HeadingSize.H4, "Transactions");
        titleHead.getElement().getStyle().setPadding(0, Style.Unit.PX);
        titleHead.setId("title-trxs");

        Container headContainer = new Container();
        headContainer.setFluid(true);
        Row headRow = new Row();
        headRow.add(dateColumn);
        headRow.add(titleColumn);
        headRow.add(pagerColumn);
        headContainer.add(headRow);
        header.add(headContainer);
        titleColumn.add(titleHead);

        table.setPageSize(10);
        table.setStriped(true);
        table.setCondensed(true);
        table.setHover(true);

        SafeHtmlCell actionCell = new SafeHtmlCell();
        Column<Transaction, SafeHtml> actionColumn = new Column<Transaction, SafeHtml>(actionCell) {
            @Override
            public SafeHtml getValue(Transaction object) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                boolean received = object.getDirection() == Transaction.Direction.RECEIVE;
                boolean paramining = received && object.getOpponent().equalsIgnoreCase(Epoch.GENESIS);
                boolean hasFee = !received;
                String title = received ? (paramining? "Paramining" : "Received") : "Sent";
                String titleClass = received ? (paramining? "purple" : "green") : "red";
                String valueId = "" + (id++);
                
                builder
                        .appendHtmlConstant("<div id=\"")
                        .appendEscaped(object.getID())
                        .appendHtmlConstant("\" class=\"item ")
                        .appendEscaped(titleClass)
                        .appendHtmlConstant("\"><div class=\"info\" onclick=\"itemClick('")
                        .appendEscaped(object.getID())
                        .appendHtmlConstant("')\"><div class=\"status ")
                        .appendEscaped(titleClass)
                        .appendHtmlConstant("\">")
                        .appendEscaped(title)
                        .appendHtmlConstant("</div><span class=\"date\"> ")
                        .appendEscaped(DATE_FORMAT.format(object.getDate()))
                        .appendHtmlConstant("</span></div><div id=\"value")
                        .appendEscaped(object.getID())
                        .appendHtmlConstant("\" class=\"value\" style=\"overflow: hidden; display: block;\"><div class=\"txid\">TX <a href=\"http://blockchain.prizm.space/?search=")
                        .appendEscaped(object.getID())
                        .appendHtmlConstant("\" target=\"_blank\">")
                        .appendEscaped(object.getID())
                        .appendHtmlConstant("</a>")
                        .appendHtmlConstant(EasyCopy.createCopyButtonHTML(object.getID()))
                        .appendHtmlConstant("</div><div class=\"wallet\">")
                        .appendEscaped(paramining?"GENESIS":(object.getOpponent() == null || object.getOpponent().isEmpty()?"Account info change":object.getOpponent()))
                        .appendHtmlConstant(EasyCopy.createCopyButtonHTML(object.getOpponent()))
                        .appendHtmlConstant("</div>");
                if (!paramining && object.getOpponent() != null && !object.getOpponent().isEmpty()) {
                    builder
                            .appendHtmlConstant("<div class=\"send-button-wrapper\"><a class=\"send-button\" href=\"javascript:setAccountToSend('")
                            .appendEscaped(object.getOpponent())
                            .appendHtmlConstant("');\">Send</a></div>");
                }
                builder
                        .appendHtmlConstant("<div class=\"message\">")
                        .appendEscaped(object.getComment() == null || (object.getComment().isEmpty()||object.getComment().equals("none"))? "no message" : object.getComment())
                        .appendHtmlConstant("</div></div><div class=\"action\" onclick=\"itemClick('")
                        .appendEscaped(object.getID())
                        .appendHtmlConstant("')\"><div class=\"data ")
                        .appendEscaped(titleClass)
                        .appendHtmlConstant("\">")
                        .appendHtmlConstant("<div class=\"change ")
                        .appendEscaped(received?"plus":"minus")
                        
                        .appendHtmlConstant("\"></div>")
                        .appendEscaped(DataUtils.formatNumber(object.getAmount()));
                if (hasFee) {
                    builder
                            .appendHtmlConstant("<span class=\"fee\">fee ")
                            .appendEscaped(DataUtils.formatNumber(object.getFee()))
                            .appendHtmlConstant("</span>");
                }
                builder.appendHtmlConstant("</div><div id=\"")
                        .appendEscaped("arrow" + object.getID())
                        .appendHtmlConstant("\" class=\"arrow down\"></div></div><div>");
                
                return builder.toSafeHtml();
            }
        };
        table.addColumn(actionColumn, "");

        pagination = new ShortPagination<>(table);

        VerticalPanel vp = new VerticalPanel();

        pagination.setPull(Pull.RIGHT);
        pagerColumn.add(pagination);
        pagination.getElement().getStyle().setMargin(0, Style.Unit.PX);

        table.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(selectionHandler);

        body.add(table);
    }
}
