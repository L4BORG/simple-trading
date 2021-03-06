/*
 * This file is part of SimpleTrading.
 * Copyright (c) 2015-2016 Matthias Werning
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.xaniox.simpletrading.i18n;

import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;

import java.util.Locale;
import java.util.Map;

public class I18NManager {
	
	/* Simple object used for synchronization when initializing
	 * the global instance of I18N */
	private static final Object initLock = new Object();
	private static I18NBuilder globalBuilder;
	private static I18N global;
	
	private Map<String, I18N> registered;
	
	/**
	 * Returns the global I18N instance used for
	 * retrieving internal messages of HeavySpleef
	 * 
	 * @return The global instance of I18N
	 */
	public static I18N getGlobal() {
		synchronized (initLock) {
			if (global == null) {
				if (globalBuilder == null) {
					throw new IllegalStateException("No global builder has been set for initializing");
				}
				
				global = globalBuilder.build();
			}
		}
		
		return global;
	}
	
	/**
	 * Sets the {@link I18NBuilder} for initializing the
	 * global I18N.<br><br>
	 * 
	 * This may throw an exception when there is already a builder set
	 * 
	 * @param builder The builder for initializing
	 * @see #getGlobal()
	 */
	public static void setGlobalBuilder(I18NBuilder builder) {
		if (globalBuilder != null) {
			throw new IllegalStateException("Global I18NBuilder has already been set");
		}
		
		I18NManager.globalBuilder = builder;
	}
	
	public I18NManager() {
		this.registered = Maps.newHashMap();
	}
	
	public void registerI18N(String name, I18N i18n) {
		Validate.isTrue(!registered.containsKey(name), "I18N instance already registered");
		
		i18n.setParent(global);
		registered.put(name, i18n);
	}
	
	public I18N registerI18N(String name, I18NBuilder builder) {
		I18N i18n = builder.build();
		registerI18N(name, i18n);
		
		return i18n;
	}
	
	public void unregisterI18N(String name) {
		Validate.isTrue(registered.containsKey(name), "I18N instance is not registered");
		
		registered.remove(name);
	}
	
	public I18N getI18N(String name) {
		return registered.get(name);
	}
	
	public void reloadAll(Locale locale) {
		if (global != null) {
			global.setLocale(locale);
			global.load();
		}
		
		for (I18N i18n : registered.values()) {
			i18n.setLocale(locale);
			i18n.load();
		}
	}
	
}