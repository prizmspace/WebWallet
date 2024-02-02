package prizm.client.bricks;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

public class ShortPagination<K> extends Pagination {
    
    private CellTable<K> table;
    protected List<K> data = new ArrayList<K>();
    private SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);

    private AnchorListItem current = new AnchorListItem(" ");
    
    private AnchorListItem next;
    private AnchorListItem prev;
    
    protected ListDataProvider<K> asyncDataProvider;

    public ListDataProvider<K> getAsyncDataProvider() {
        return asyncDataProvider;
    }
        
    public void insert (Collection<K> elements) {
        data.addAll(0, elements);
        fixPager();
    }
    
    public void append (List<K> elements) {
        data.addAll(elements);
        fixPager();
    }
    
    public void refresh(List<K> elements) {
        Map<Integer, K> replace = new TreeMap<Integer, K>();
        for (K element : elements) {
            int counter = data.size();
            for (int i = 0; i < counter; i++) {
                K item = data.get(i);
                if (element.equals(item)) {
                    replace.put(i, element);
                }
            }
        }
        for (Entry<Integer,K> entry : replace.entrySet()) {
            data.set(entry.getKey(), entry.getValue());
        }
    }
    
    public void replace (Collection<K> elements) {
        data.clear();
        data = asyncDataProvider.getList();
        data.addAll(elements);
        fixPager();
    }
    
    public void lastPage() {
        fixPager();
        pager.setPage(pager.getPageCount()-1);
        fixPager();
    }
    
    public void fixPagerLittle() {
        asyncDataProvider.flush();
    }
    
    public void fixPager() {
        asyncDataProvider.flush();
        asyncDataProvider.refresh();
        if (pager.getPageCount() < 2) {
            setVisible(false);
            return;
        } else {
            setVisible(true);
            current.setText((pager.getPage() + 1) + " | " + pager.getPageCount());
        }
        if (pager.hasNextPage()) {
            next.setEnabled(true);
        } else {
            next.setEnabled(false);
        }
        if (pager.hasPreviousPage()) {
            prev.setEnabled(true);
        } else {
            prev.setEnabled(false);
        }
    }

    public List<K> getData() {
        return data;
    }

    @Override
    public final AnchorListItem addNextLink() {
        final AnchorListItem listItem = new AnchorListItem(">");
        add(listItem);
        return listItem;        
    }

    @Override
    public final AnchorListItem addPreviousLink() {
        final AnchorListItem listItem = new AnchorListItem("<");
        insert(listItem, 0);
        return listItem;
    }
    
    
    
    public ShortPagination(final CellTable<K> table) {
        this.table = table;
        add(current);
        setVisible(false);
        current.setActive(true);
        next = addNextLink();
        prev = addPreviousLink();
        current.getElement().getStyle().setPaddingTop(2, Style.Unit.PX);
        current.getElement().getStyle().setPaddingBottom(2, Style.Unit.PX);
                
        asyncDataProvider = new ListDataProvider<K>() {
            @Override
            protected void onRangeChanged(HasData<K> display) {
                int start = display.getVisibleRange().getStart();
                int end = start + display.getVisibleRange().getLength();
                end = end >= data.size() ? data.size() : end;
                List<K> sub = data.subList(start, end);
                updateRowData(start, sub);
            }
        };
        
        asyncDataProvider.addDataDisplay(table);
        this.data = asyncDataProvider.getList();
        pager.setDisplay(table);
        next.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                pager.nextPage();
            }
        });
        prev.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                pager.previousPage();
            }
        });
        RangeChangeEvent.Handler rangeHandler = new RangeChangeEvent.Handler() {

            @Override
            public void onRangeChange(RangeChangeEvent event) {
                fixPager();
            }
        };
        table.addRangeChangeHandler(rangeHandler);
    }
        
}
