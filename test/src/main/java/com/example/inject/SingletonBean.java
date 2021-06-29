package com.example.inject;

import java.util.ArrayList;
import java.util.List;

public class SingletonBean {

	private List<IBean> list = new ArrayList<>();

	public void add(IBean iBean) {
		list.add(iBean);
	}

	public List<String> console() {
		List<String> list = new ArrayList<>();
		for (IBean iBean : this.list) {
			list.add(iBean.test());
		}
		return list;
	}
}
