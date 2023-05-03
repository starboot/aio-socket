package cn.starboot.socket.utils.page;

import cn.starboot.socket.utils.convert.Converter;
import cn.starboot.socket.utils.lock.SetWithLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class PageUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(PageUtils.class);

	public static <T> Page<T> fromList(List<T> list,
									   Integer pageIndex,
									   Integer pageSize) {
		return fromList(list, pageIndex, pageSize, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> Page<T> fromList(List<?> list,
									   Integer pageIndex,
									   Integer pageSize,
									   Converter<T> converter) {
		if (list == null) {
			return null;
		}

		Page<Object> page = pre(list, pageIndex, pageSize);

		List<Object> pageData = page.getList();
		if (pageData == null) {
			return (Page<T>) page;
		}

		int startIndex = Math.min((page.getPageNumber() - 1) * page.getPageSize(), list.size());
		int endIndex = Math.min(page.getPageNumber() * page.getPageSize(), list.size());

		for (int i = startIndex; i < endIndex; i++) {
			if (converter != null) {
				pageData.add(converter.convert(list.get(i)));
			} else {
				pageData.add(list.get(i));
			}

		}
		page.setList(pageData);
		return (Page<T>) page;
	}

	public static <T> Page<T> fromSet(Set<T> set,
									  Integer pageIndex,
									  Integer pageSize) {
		return fromSet(set, pageIndex, pageSize, null);
	}

	public static <T> Page<T> fromSet(Set<?> set,
									  Integer pageIndex,
									  Integer pageSize,
									  Converter<T> converter) {
		if (set == null) {
			return null;
		}

		Page<Object> page = pre(set, pageIndex, pageSize);

		List<Object> pageData = page.getList();
		if (pageData == null) {
			return (Page<T>) page;
		}

		int startIndex = Math.min((page.getPageNumber() - 1) * page.getPageSize(), set.size());
		int endIndex = Math.min(page.getPageNumber() * page.getPageSize(), set.size());

		int i = 0;
		for (Object t : set) {
			if (i >= endIndex) {
				break;
			}
			if (i < startIndex) {
				i++;
				continue;
			}

			if (converter != null) {
				pageData.add(converter.convert(t));
			} else {
				pageData.add(t);
			}

			i++;
			continue;
		}
		page.setList(pageData);
		return (Page<T>) page;
	}

	public static <T> Page<T> fromSetWithLock(SetWithLock<T> setWithLock,
											  Integer pageIndex,
											  Integer pageSize) {
		return fromSetWithLock(setWithLock, pageIndex, pageSize, null);

	}

	public static <T> Page<T> fromSetWithLock(SetWithLock<?> setWithLock,
											  Integer pageIndex,
											  Integer pageSize,
											  Converter<T> converter) {
		if (setWithLock == null) {
			return null;
		}
		Lock lock = setWithLock.readLock();
		lock.lock();
		try {
			@SuppressWarnings("unchecked")
			Set<Object> set = (Set<Object>) setWithLock.getObj();
			return fromSet(set, pageIndex, pageSize, converter);
		} finally {
			lock.unlock();
		}
	}

	private static Page<Object> pre(Collection<?> allList,
									Integer pageIndex,
									Integer pageSize) {
		if (allList == null) {
			return new Page<>(null, pageIndex, pageSize, 0);
		}
		pageSize = processPageSize(pageSize);
		pageIndex = processpageNumber(pageIndex);
		int recordCount = allList.size();
		if (pageSize > recordCount) {
			pageSize = recordCount;
		}
		List<Object> pageData = new ArrayList<>(pageSize);
		return new Page<>(pageData, pageIndex, pageSize, recordCount);
	}

	private static int processpageNumber(Integer pageIndex) {
		return pageIndex <= 0 ? 1 : pageIndex;
	}

	private static int processPageSize(Integer pageSize) {
		return pageSize <= 0 ? Integer.MAX_VALUE : pageSize;
	}

}
