/*
 * Copyright (c) 2017 Adrian Siekierka
 *
 * This file is part of Unlimited Chisel Works.
 *
 * Unlimited Chisel Works is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unlimited Chisel Works is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Unlimited Chisel Works.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.asie.ucw;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUCWProxy extends ItemBlock {
	private UCWBlockRule rule;
	private IBlockState base;

	public ItemUCWProxy(Block block) {
		super(block);
		rule = UCWObjectBroker.get().getRule();
		base = UCWObjectBroker.get().getBase();
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		getItemThrough().addInformation(
				UCWUtils.copyChangeItem(stack, getItemThrough()),
				worldIn, tooltip, flagIn
		);
	}

	@Override
	public String getUnlocalizedName() {
		return getItemFrom().getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return getItemFrom().getUnlocalizedName(new ItemStack(base.getBlock().getItemDropped(base, UnlimitedChiselWorks.RAND, 0), 1, base.getBlock().damageDropped(base)));
		} catch (Exception e) {
			e.printStackTrace();
			return getUnlocalizedName();
		}
	}
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		try {
			return getItemFrom().getItemStackDisplayName(new ItemStack(base.getBlock().getItemDropped(base, UnlimitedChiselWorks.RAND, 0), 1, base.getBlock().damageDropped(base)));
		} catch (Exception e) {
			e.printStackTrace();
			return getUnlocalizedName();
		}
	}

	private Item getItemFrom() {
		return Item.getItemFromBlock(rule.fromBlock);
	}

	private Item getItemThrough() {
		return Item.getItemFromBlock(rule.throughBlock);
	}

	@Override
	public int getMetadata(int damage) {
		if (rule.through.get(damage) != null) {
			return damage;
		} else {
			return 0;
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		Item origItem = getItemThrough();
		NonNullList<ItemStack> proxyList = NonNullList.create();
		origItem.getSubItems(tab, proxyList);
		for (ItemStack stack : proxyList) {
			if (stack.getItem() == origItem) {
				// FIXME: Dirt#9 doesn't really work well :-(
				if (rule.throughBlock.getRegistryName().toString().equals("chisel:dirt") && stack.getItemDamage() == 9) {
					continue;
				}

				items.add(UCWUtils.copyChangeItem(stack, this));
			}
		}
	}
}