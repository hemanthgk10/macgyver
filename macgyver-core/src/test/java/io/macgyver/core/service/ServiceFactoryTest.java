package io.macgyver.core.service;

import java.lang.reflect.InvocationTargetException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

public class ServiceFactoryTest {

	Logger log = LoggerFactory.getLogger(ServiceFactoryTest.class);

	public static class Foo {

		String name;
		int age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

	}

	@Test
	public void testIt() throws IllegalAccessException,
			InvocationTargetException {

		Foo f = new Foo();

		BeanWrapperImpl x = new BeanWrapperImpl(f);
		x.setPropertyValue("age", "33");

		Assertions.assertThat(f.getAge()).isEqualTo(33);
	}
}
