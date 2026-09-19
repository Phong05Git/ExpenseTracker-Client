package com.example.expensetracker.util;

import com.example.expensetracker.R;

public final class IconResolver {

    private IconResolver() {
    }

    public static int getIconResId(String icon) {
        if (icon == null || icon.isBlank()) {
            return R.drawable.ic_tag;
        }

        switch (icon) {
            case "restaurant":
                return R.drawable.ic_restaurant;
            case "local_cafe":
                return R.drawable.ic_local_cafe;
            case "fastfood":
                return R.drawable.ic_fastfood;
            case "shopping_cart":
                return R.drawable.ic_shopping_cart;
            case "shopping_bag":
                return R.drawable.ic_shopping_bag;
            case "directions_car":
                return R.drawable.ic_directions_car;
            case "directions_bus":
                return R.drawable.ic_directions_bus;
            case "local_gas_station":
                return R.drawable.ic_local_gas_station;
            case "home":
                return R.drawable.ic_home;
            case "lightbulb":
                return R.drawable.ic_lightbulb;
            case "phone_android":
                return R.drawable.ic_phone_android;
            case "laptop":
                return R.drawable.ic_laptop;
            case "sports_esports":
                return R.drawable.ic_sports_esports;
            case "movie":
                return R.drawable.ic_movie;
            case "music_note":
                return R.drawable.ic_music_note;
            case "sports_soccer":
                return R.drawable.ic_sports_soccer;
            case "school":
                return R.drawable.ic_school;
            case "menu_book":
                return R.drawable.ic_menu_book;
            case "medical_services":
                return R.drawable.ic_medical_services;
            case "local_pharmacy":
                return R.drawable.ic_local_pharmacy;
            case "pets":
                return R.drawable.ic_pets;
            case "card_giftcard":
                return R.drawable.ic_card_giftcard;
            case "payments":
                return R.drawable.ic_payments;
            case "account_balance":
                return R.drawable.ic_account_balance;
            default:
                return R.drawable.ic_tag;
        }
    }
}