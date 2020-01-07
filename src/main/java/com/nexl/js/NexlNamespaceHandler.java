package com.nexl.js;/*
 * User: eldad
 * Date: 09/12/2019
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA.
 */

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NexlNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("nexl",new NexlRequestBeanDefinitionParser());
	}
}