package com.nexl.js;/*
 * User: eldad
 * Date: 09/12/2019
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA.
 */

import com.nexl.Nexl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

public class NexlRequestBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		return parseComponentElement(element);
	}


	private static AbstractBeanDefinition parseComponentElement(Element element) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(NexlResourceFactoryBean.class);
		factory.addPropertyValue("parent", parseComponent(element));
		List<Element> childElements = DomUtils.getChildElementsByTagName(element, "nexl");
		if (childElements != null && !childElements.isEmpty()) {
			parseChildComponents(childElements, factory);
		}
		return factory.getBeanDefinition();
	}

	private static BeanDefinition parseComponent(Element element) {
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(Nexl.class);
		component.addPropertyValue("name", element.getAttribute("name"));
		component.addPropertyValue("url", element.getAttribute("url"));
		return component.getBeanDefinition();
	}

	private static void parseChildComponents(List<Element> childElements, BeanDefinitionBuilder factory) {
		ManagedList<BeanDefinition> children = new ManagedList<>(childElements.size());
		for (Element element : childElements) {
			children.add(parseComponentElement(element));
		}
		factory.addPropertyValue("children", children);
	}

	@Override
	protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {
		super.postProcessComponentDefinition(componentDefinition);
	}
}