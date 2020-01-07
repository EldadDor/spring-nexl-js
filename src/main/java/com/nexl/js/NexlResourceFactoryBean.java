package com.nexl.js;/*
 * User: eldad
 * Date: 09/12/2019
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA.
 */

import com.idi.astro.nexl.NexlHttpRequestKt;
import com.nexl.Nexl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.slf4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class NexlResourceFactoryBean implements FactoryBean<Nexl<Object>> {
	private static final Logger log = getLogger(NexlResourceFactoryBean.class);
	private Nexl parent;
	private List<Nexl> children;
	private Class<?> typeOfResource;

	public void setParent(Nexl parent) {
		this.parent = parent;
	}

	public void setTypeOfResource(Class<?> typeOfResource) {
		this.typeOfResource = typeOfResource;
	}

	public Class<?> getTypeOfResource() {
		if (typeOfResource == null) {
			typeOfResource = String.class;
		}
		return typeOfResource;
	}

	public void setChildren(List<Nexl> children) {
		this.children = children;
	}

	@Override
	public Nexl<Object> getObject() {
		NexlHttpRequestKt nexlHttpRequestKt = new NexlHttpRequestKt();
		KClass kotlinClass = Reflection.createKotlinClass(getTypeOfResource());
		Object value = nexlHttpRequestKt.fetchIt(parent.getUrl(), kotlinClass);
		parent.setResource(value);
		if (this.children != null && !this.children.isEmpty()) {
			for (Nexl child : children) {
				this.parent.addNexl(child);
			}
		}
		return this.parent;
	}

	@Override
	public Class<Nexl> getObjectType() {
		return Nexl.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}