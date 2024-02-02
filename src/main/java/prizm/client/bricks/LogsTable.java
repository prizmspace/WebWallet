package prizm.client.bricks;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
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
import prizm.client.Prizm;
import prizm.client.pojo.Transaction;
import prizm.client.service.DataUtils;
import prizm.client.service.Epoch;

public class LogsTable extends Panel {
    
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

    private SingleSelectionModel<Transaction> selectionModel = new SingleSelectionModel<Transaction>();
    
    private SelectionChangeEvent.Handler selectionHandler = new SelectionChangeEvent.Handler() {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            
        }
    };
    
    public void unSelectAll() {
        selectionModel.clear();
    }
    
    public void update(List<Transaction> transactions) {
        pagination.replace(Prizm.checkSavedTransaction(transactions));
        pagination.insert(Prizm.getSavedTransaction());
    }
    
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("HH:mm / dd.MM.yyyy");
    
    private static void parseTransaction(SafeHtmlBuilder builder, Transaction transaction) {
        
    }
    
    public LogsTable() {
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

        SafeHtmlCell adminCell = new SafeHtmlCell();
        Column<Transaction, SafeHtml> adminColumn = new Column<Transaction, SafeHtml>(adminCell) {
            @Override
            public SafeHtml getValue(Transaction object) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                if (object.getDirection() == Transaction.Direction.RECEIVE) {
                    if (object.getOpponent().equals(Epoch.GENESIS)) {
                        builder.appendHtmlConstant("<div style=\"white-space:nowrap;\" class=\"text-warning\"> <i class=\"fa fa-university\"></i> ");
                        builder.appendHtmlConstant("</div>");
                    } else {
                        builder.appendHtmlConstant("<div style=\"white-space:nowrap;\" class=\"text-success\"> <i class=\"fa fa-arrow-down\"></i> ");
                        builder.appendHtmlConstant("</div>");                        
                    }
                }
                if (object.getDirection() == Transaction.Direction.SEND) {
                    builder.appendHtmlConstant("<div style=\"white-space:nowrap;\" class=\"text-danger\"> <i class=\"fa fa-arrow-up\"></i> ");
                    builder.appendHtmlConstant("</div>");
                }
                return builder.toSafeHtml();
            }
        };
        table.addColumn(adminColumn, "^");
        
        SafeHtmlCell actionCell = new SafeHtmlCell();
        Column<Transaction, SafeHtml> actionColumn = new Column<Transaction, SafeHtml>(actionCell) {
            @Override
            public SafeHtml getValue(Transaction object) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                if (object.getDirection() == Transaction.Direction.RECEIVE) {
                    if (object.getOpponent().equals(Epoch.GENESIS)) {
                        builder.appendHtmlConstant("<span class=\"text-warning\"> ");
                        builder.appendEscaped(DATE_FORMAT.format(object.getDate()));
                        String paratax = "";
                        builder.appendHtmlConstant("</span><BR/> <b>PARAMINING <span class=\"text-success\">+"+DataUtils.formatNumber(object.getAmount())+" PZM"+paratax+"</span></b> ");
                    } else {
                        builder.appendHtmlConstant("<span class=\"text-success\"> ");
                        builder.appendEscaped(DATE_FORMAT.format(object.getDate()));
                        builder.appendHtmlConstant("</span><BR/> <b>");
                        builder.appendEscaped(object.getOpponent());
                        builder.appendHtmlConstant(" <span class=\"text-success\">+");
                        builder.appendEscaped(DataUtils.formatNumber(object.getAmount()));
                        builder.appendHtmlConstant(" PZM</span></b><BR/> ");
                        builder.appendEscaped(object.getComment());
                    }
                }
                if (object.getDirection() == Transaction.Direction.SEND) {
                        builder.appendHtmlConstant("<span class=\"text-danger\"> ");
                        builder.appendEscaped(DATE_FORMAT.format(object.getDate()));
                        builder.appendHtmlConstant("</span><BR/> <b>");
                        builder.appendEscaped(object.getOpponent());
                        builder.appendHtmlConstant(" <span class=\"text-danger\">-");
                        builder.appendEscaped(DataUtils.formatNumber(object.getAmount()));
                        builder.appendHtmlConstant(" PZM (fee: ");
                        builder.appendEscaped(DataUtils.formatNumber(object.getFee()));
                        builder.appendHtmlConstant(" PZM)</span></b><BR/> ");
                        builder.appendEscaped(object.getComment());
                }
                return builder.toSafeHtml();
            }
        };
        table.addColumn(actionColumn, "Action");
                        
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
