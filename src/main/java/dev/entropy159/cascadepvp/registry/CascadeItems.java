package dev.entropy159.cascadepvp.registry;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.entropy159.cascadepvp.items.RiftwandItem;
import dev.entropy159.cascadepvp.items.weapon.*;
import dev.entropy159.cascadepvp.items.weapon.hexblade.HexbladeItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import static dev.entropy159.cascadepvp.CascadePVP.REGISTRATE;

public class CascadeItems {
    public static final ItemEntry<Item> HEXBLADE_TEMPLATE = REGISTRATE.item("hexblade_template", Item::new).recipe(template(Items.EMERALD_BLOCK, Items.BLAZE_ROD)).register();
    public static final ItemEntry<Item> REAPER_SCYTHE_TEMPLATE = REGISTRATE.item("reaper_scythe_template", Item::new).recipe(template(Items.GHAST_TEAR, Items.BLAZE_ROD)).register();
    public static final ItemEntry<Item> KINGS_WILL_TEMPLATE = REGISTRATE.item("kings_will_template", Item::new).recipe(template(Items.GOLDEN_APPLE, Items.SUGAR)).register();
    public static final ItemEntry<Item> SHADOW_KARAMBIT_TEMPLATE = REGISTRATE.item("shadow_karambit_template", Item::new).recipe(template(Items.FERMENTED_SPIDER_EYE, Items.GOLDEN_CARROT)).register();
    public static final ItemEntry<Item> ABYSSAL_IMPACT_TEMPLATE = REGISTRATE.item("abyssal_impact_template", Item::new).recipe(template(Items.BREEZE_ROD, Items.IRON_BLOCK)).register();
    public static final ItemEntry<Item> BOOMBOW_TEMPLATE = REGISTRATE.item("boombow_template", Item::new).lang("Sparky Sparky Boom Bow Template").recipe(template(Items.TNT, Items.OBSIDIAN)).register();
    public static final ItemEntry<Item> BOW_OF_THE_GALADHRIM_TEMPLATE = REGISTRATE.item("bow_of_the_galadhrim_template", Item::new).register();

    public static final ItemEntry<HexbladeItem> HEXBLADE = REGISTRATE.item("hexblade", HexbladeItem::new).model(existing()).color(() -> () -> (stack, index) -> switch (index) {
        case 1 -> HexbladeItem.getAspectColor(stack);
        case 2 -> HexbladeItem.getNextAspectColor(stack);
        default -> 0xFFFFFFFF;
    }).recipe(upgrade(Items.DIAMOND_SWORD, HEXBLADE_TEMPLATE)).tag(ItemTags.SWORDS).register();
    public static final ItemEntry<ReaperScytheItem> REAPER_SCYTHE = REGISTRATE.item("reaper_scythe", ReaperScytheItem::new).model(existing()).recipe(upgrade(Items.DIAMOND_SWORD, REAPER_SCYTHE_TEMPLATE)).tag(ItemTags.SWORDS).register();
    public static final ItemEntry<KingsWillItem> KINGS_WILL = REGISTRATE.item("kings_will", KingsWillItem::new).lang("King's Will").model(handheld()).recipe(upgrade(Items.DIAMOND_SWORD, KINGS_WILL_TEMPLATE)).tag(ItemTags.SWORDS).register();
    public static final ItemEntry<ShadowKarambitItem> SHADOW_KARAMBIT = REGISTRATE.item("shadow_karambit", ShadowKarambitItem::new).model(existing()).recipe(upgrade(Items.DIAMOND_SWORD, SHADOW_KARAMBIT_TEMPLATE)).tag(ItemTags.SWORDS).register();
    public static final ItemEntry<AbyssalImpactItem> ABYSSAL_IMPACT = REGISTRATE.item("abyssal_impact", AbyssalImpactItem::new).model((ctx, provider) -> {
        float scale = 1.3f;
        provider.handheld(ctx::get).transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0, -90, 55).scale(scale, scale, scale).translation(0, 8.5f, -0.5f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(0, 90, -55).scale(scale, scale, scale).translation(0, 8.5f, -0.5f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 90, -25).scale(scale, scale, scale).translation(1.13f, 3.2f, 1.13f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90, 25).scale(scale, scale, scale).translation(1.13f, 3.2f, 1.13f).end()
                .end();
    }).recipe(upgrade(Items.DIAMOND_AXE, ABYSSAL_IMPACT_TEMPLATE)).tag(ItemTags.AXES).register();
    public static final ItemEntry<BoombowItem> BOOMBOW = REGISTRATE.item("boombow", BoombowItem::new).model(bow()).recipe(upgrade(Items.BOW, BOOMBOW_TEMPLATE)).lang("Sparky Sparky Boom Bow").tag(Tags.Items.TOOLS_BOW, ItemTags.BOW_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE).register();
    public static final ItemEntry<BowOfTheGaladhrim> BOW_OF_THE_GALADHRIM = REGISTRATE.item("bow_of_the_galadhrim", BowOfTheGaladhrim::new).model(bowOverlay()).color(() -> () -> (stack, index) -> (index == 1 && stack.getOrDefault(CascadeDataComponents.SUPERCHARGED, false)) ? 0xFFFF0000 : 0xFF757575).recipe(template(Items.SPYGLASS, Items.WIND_CHARGE)).recipe(upgrade(Items.BOW, BOW_OF_THE_GALADHRIM_TEMPLATE)).tag(Tags.Items.TOOLS_BOW, ItemTags.BOW_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE).register();

    public static final ItemEntry<RiftwandItem> RIFTWAND = REGISTRATE.item("riftwand", RiftwandItem::new).model(handheld()).recipe(shaped(RecipeCategory.TOOLS, RegistrateRecipeProvider.has(Tags.Items.GEMS_AMETHYST), "  D", " A ", "A  ", new Tuple<>('D', Ingredient.of(Tags.Items.GEMS_DIAMOND)), new Tuple<>('A', Ingredient.of(Tags.Items.GEMS_AMETHYST)))).register();

    public static void init() {
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> handheld() {
        return (ctx, provider) -> provider.handheld(ctx::get);
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> existing() {
        return (ctx, provider) -> provider.withExistingParent(ctx.getName() + "_gen", toItem(ctx.getId()));
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> bow() {
        return (ctx, provider) -> {
            provider.withExistingParent(ctx.getName(), "item/bow").texture("layer0", itemFolder(ctx.getId(), "base"))
                    .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1).model(provider.withExistingParent(ctx.getName() + "_0", toItem(ctx.getId())).texture("layer0", itemFolder(ctx.getId(), "pulling_0"))).end()
                    .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1).predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65f).model(provider.withExistingParent(ctx.getName() + "_1", toItem(ctx.getId())).texture("layer0", itemFolder(ctx.getId(), "pulling_1"))).end()
                    .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1).predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9f).model(provider.withExistingParent(ctx.getName() + "_2", toItem(ctx.getId())).texture("layer0", itemFolder(ctx.getId(), "pulling_2"))).end();
        };
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> bowOverlay() {
        return (ctx, provider) -> {
            provider.withExistingParent(ctx.getName(), "item/bow").texture("layer0", itemFolder(ctx.getId(), "base")).texture("layer1", itemFolder(ctx.getId(), "base_overlay"))
                    .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1).model(provider.withExistingParent(ctx.getName() + "_0", toItem(ctx.getId())).texture("layer0", itemFolder(ctx.getId(), "pulling_0")).texture("layer1", itemFolder(ctx.getId(), "pulling_0_overlay"))).end()
                    .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1).predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65f).model(provider.withExistingParent(ctx.getName() + "_1", toItem(ctx.getId())).texture("layer0", itemFolder(ctx.getId(), "pulling_1")).texture("layer1", itemFolder(ctx.getId(), "pulling_1_overlay"))).end()
                    .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1).predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9f).model(provider.withExistingParent(ctx.getName() + "_2", toItem(ctx.getId())).texture("layer0", itemFolder(ctx.getId(), "pulling_2")).texture("layer1", itemFolder(ctx.getId(), "pulling_2_overlay"))).end();
        };
    }

    private static ResourceLocation toItem(ResourceLocation id) {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    private static ResourceLocation itemFolder(ResourceLocation id, String path) {
        return toItem(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/" + path));
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> upgrade(ItemLike base, ItemLike template) {
        DataIngredient source = DataIngredient.ingredient(Ingredient.of(base), base);
        return (ctx, provider) -> SmithingTransformRecipeBuilder.smithing(Ingredient.of(template), Ingredient.of(base), Ingredient.EMPTY, RecipeCategory.COMBAT, ctx.get()).unlocks("has_" + provider.safeName(source), source.getCriterion(provider)).save(provider, provider.safeId(ctx.get()));
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> template(ItemLike ingredient1, ItemLike ingredient2) {
        return template(Ingredient.of(ingredient1), Ingredient.of(ingredient2));
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> template(Ingredient ingredient1, Ingredient ingredient2) {
        return shaped(RecipeCategory.COMBAT, RegistrateRecipeProvider.has(Items.DIAMOND), "DDD", "ADB", "DDD", new Tuple<>('D', Ingredient.of(Items.DIAMOND)), new Tuple<>('A', ingredient1), new Tuple<>('B', ingredient2));
    }

    private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> shaped(RecipeCategory category, Criterion<InventoryChangeTrigger.TriggerInstance> criterion, String row1, String row2, String row3, Tuple<Character, Ingredient>... ingredients) {
        return (ctx, provider) -> {
            var builder = ShapedRecipeBuilder.shaped(category, ctx.get()).pattern(row1).pattern(row2).pattern(row3);
            for (var tuple : ingredients) {
                builder = builder.define(tuple.getA(), tuple.getB());
            }
            builder = builder.unlockedBy("has_criterion_" + provider.safeName(ctx.getId()), criterion);
            builder.save(provider, provider.safeId(ctx.get()));
        };
    }
}
