package cn.starboot.socket.utils.page;

import cn.starboot.socket.utils.convert.Converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {

	/* uid */
	private static final long serialVersionUID = -683881545722557505L;

	private List<T> list = null;//当前页的数据
	private Integer pageNumber; //当前页码，从1开始，如果值小于等于0，则视为1
	private Integer pageSize;   //每页记录数
	private Integer totalRow;   //总条数
	private Integer totalPage;  //总页数

	public Page() {

	}

	public Page(List<T> list, Integer pageNumber, Integer pageSize, Integer totalRow) {
		this.list = list;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalRow = totalRow;
	}

	public Page(List<?> list, Integer pageNumber, Integer pageSize, Integer totalRow, Converter<T> converter) {
		if (list != null && list.size() > 0 && converter != null) {
			this.list = new ArrayList<>(list.size());
			for (Object object : list) {
				T t = converter.convert(object);
				this.list.add(t);
			}
		}

		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalRow = totalRow;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(Integer totalRow) {
		this.totalRow = totalRow;
	}

	public Integer getTotalPage() {
		double result = Math.ceil(((double) (totalRow) / pageSize));
		totalPage = (int) result;
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public boolean isFirstPage() {
		return pageNumber <= 1;
	}

	public boolean isLastPage() {
		return pageNumber >= getTotalPage();
	}

	@Override
	public String toString() {
		return String.format("Page [list=%s, pageNumber=%s, pageSize=%s, totalRow=%s, totalPage=%s]", getList(), getPageNumber(), getPageSize(), getTotalRow(), getTotalPage());
	}
}
