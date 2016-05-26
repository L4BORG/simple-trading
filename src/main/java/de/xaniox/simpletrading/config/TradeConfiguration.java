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
package de.xaniox.simpletrading.config;

import com.google.common.collect.Lists;
import de.xaniox.simpletrading.ItemControlManager;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public class TradeConfiguration { 
	
	private static final String BLOCKDATA_SEPERATOR = ":";
	private static final String PLAYERNAME_PLACEHOLDER = "@p";
    public static final int NO_MAX_DISTANCE = -1;
    public static final int CURRENT_CONFIG_VERSION = 1;
    public static final String DESTINATION_FILE_NAME = "config.yml";
    public static final String CLASSPATH_RESOURCE_NAME = "/config.yml";

    private ItemStackData acceptBlockData;
	private ItemStackData declineBlockData;
	private ItemStackData seperatorBlockData;
    private Locale locale;
	private String inventoryName;
    private int moneyValue1;
    private int moneyValue2;
    private int moneyValue3;
    private int expValue1;
    private int expValue2;
    private int expValue3;
	private int maximumTradeDistance;
	private boolean allowCreativeTrading;
	private int timeout;
	private boolean useXpTrading;
	private ItemControlManager.ItemControlMode controlMode;
	private List<ItemStackData> controlItems;
	private List<String> controlLores;
	
	public TradeConfiguration(Configuration config) {
		loadByConfiguration(config);
	}
	
	public void loadByConfiguration(Configuration config) {
		ConfigurationSection blockSection = config.getConfigurationSection("blocks");
		acceptBlockData = ItemStackData.fromConfigString(blockSection.getString("accept", "ink_sack:10"), BLOCKDATA_SEPERATOR);
		declineBlockData = ItemStackData.fromConfigString(blockSection.getString("decline", "ink_sack:1"), BLOCKDATA_SEPERATOR);
		seperatorBlockData = ItemStackData.fromConfigString(blockSection.getString("seperator", "barrier"), BLOCKDATA_SEPERATOR);

        ConfigurationSection localizationSection = config.getConfigurationSection("localization");
        String localeString = localizationSection.getString("locale");
        locale = parseLocale(localeString);

		ConfigurationSection inventorySection = config.getConfigurationSection("inventory");
		inventoryName = inventorySection.getString("name", "SimpleTrading - @p");
        moneyValue1 = inventorySection.getInt("money-value-1", 50);
        moneyValue2 = inventorySection.getInt("money-value-2", 100);
        moneyValue3 = inventorySection.getInt("money-value-3", 500);
        expValue1 = inventorySection.getInt("exp-value-1", 5);
        expValue2 = inventorySection.getInt("exp-value-2", 50);
        expValue3 = inventorySection.getInt("exp-value-3", 100);

		ConfigurationSection globalSection = config.getConfigurationSection("global");
		maximumTradeDistance = globalSection.getInt("max-distance", 15);
		allowCreativeTrading = globalSection.getBoolean("creative-trading", true);
		timeout = globalSection.getInt("timeout", 60);
		useXpTrading = globalSection.getBoolean("use-xp-trading", true);
		
		ConfigurationSection itemControlSection = config.getConfigurationSection("item-control");
		controlMode = ItemControlManager.ItemControlMode.getMode(itemControlSection.getString("control-mode"), ItemControlManager.ItemControlMode.BLACKLIST);
		List<String> controlItemStringList = itemControlSection.getStringList("item-list");
		controlItems = Lists.newArrayList();
		
		for (String controlItemString : controlItemStringList) {
			controlItems.add(ItemStackData.fromConfigString(controlItemString, BLOCKDATA_SEPERATOR));
		}
		
		controlLores = itemControlSection.getStringList("item-lore");
	}

    private static Locale parseLocale(String localeString) {
        String[] parts = localeString.split("_");

        String language = parts[0];
        String country = parts.length > 1 ? parts[1] : null;
        String variant = parts.length > 2 ? parts[2] : null;

        Locale locale;

        if (country == null && variant == null) {
            locale = new Locale(language);
        } else if (country != null && variant == null) {
            locale = new Locale(language, country);
        } else {
            locale = new Locale(language, country, variant);
        }

        return locale;
    }
	
	public ItemStackData getAcceptBlockData() {
		return acceptBlockData;
	}
	
	public ItemStackData getDeclineBlockData() {
		return declineBlockData;
	}
	
	public ItemStackData getSeperatorBlockData() {
		return seperatorBlockData;
	}

    public Locale getLocale() {
        return locale;
    }

    public String getInventoryName(String nameReplacement) {
		return inventoryName.replace(PLAYERNAME_PLACEHOLDER, nameReplacement);
	}

    public int getMoneyValue1() {
        return moneyValue1;
    }

    public int getMoneyValue2() {
        return moneyValue2;
    }

    public int getMoneyValue3() {
        return moneyValue3;
    }

    public int getExpValue1() {
        return expValue1;
    }

    public int getExpValue2() {
        return expValue2;
    }

    public int getExpValue3() {
        return expValue3;
    }

    public int getMaximumTradeDistance() {
		return maximumTradeDistance;
	}
	
	public boolean allowsCreativeTrading() {
		return allowCreativeTrading;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public boolean usesXpTrading() {
		return useXpTrading;
	}
	
	public ItemControlManager.ItemControlMode getItemControlMode() {
		return controlMode;
	}
	
	public List<ItemStackData> getItemControlList() {
		return controlItems;
	}
	
	public List<String> getItemControlLoreList() {
		return controlLores;
	}
	
	public static class ItemStackData {
		
		private Material material;
		private byte data;
		
		public ItemStackData(Material material, byte data) {
			this.material = material;
			this.data = data;
		}
		
		@SuppressWarnings("deprecation")
		public static ItemStackData fromConfigString(String configStr, String seperator) {
			String[] components = configStr.split(seperator);
			String materialString = components[0];
			Material material = null;
			byte data = 0;
			
			for (Material mat : Material.values()) {
				if (mat.name().equalsIgnoreCase(materialString) ||
					mat.name().replace("_", "").equalsIgnoreCase(materialString)) {
					material = mat;
				}
			}
			
			if (material == null) {
				try {
					int legacyId = Integer.parseInt(materialString);
					material = Material.getMaterial(legacyId);
				} catch (NumberFormatException nfe) {
					// Give up
					throw new IllegalArgumentException("Config-String \"" + configStr + "\" material/block-id is invalid");
				}
			}
			
			if (components.length > 1) {
				try {
					int legacyData = Integer.parseInt(components[1]);
					data = (byte) legacyData;
				} catch (NumberFormatException nfe) {
					throw new IllegalArgumentException("Config-String \"" + configStr + "\" is invalid: Illegal block data");
				}
			}
			
			return new ItemStackData(material, data);
		}
		
		public Material getMaterial() {
			return material;
		}
		
		public byte getData() {
			return data;
		}

		public ItemStack newItemStack() {
			return newItemStack(1);
		}
		
		@SuppressWarnings("deprecation")
		public ItemStack newItemStack(int amount) {
			ItemStack stack = new ItemStack(material.getId(), amount, data);
			return stack;
		}
		
	}
	
}
