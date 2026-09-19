package com.example.expensetracker.presentation.ui.bottomsheet;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel;
import com.example.expensetracker.util.IconResolver;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditCategoryBottomSheet extends BottomSheetDialogFragment {

    public static final String CATEGORY_CHANGED_RESULT = "category_changed";
    public static final String CATEGORY_CHANGED_BUNDLE = "changed";

    private CategoryViewModel viewModel;
    private Category category;

    private TextInputEditText editName;
    private MaterialButton buttonExpense;
    private MaterialButton buttonIncome;
    private TextView tvIconPreview;
    private TextView tvCategoryIconName;
    private View viewCategoryColorPreview;
    private TextView tvCategoryColorName;

    private int selectedType = 2;
    private String selectedIcon = "restaurant";
    private String selectedColor = "#43A047";

    public static AddEditCategoryBottomSheet newCreateInstance() {
        return new AddEditCategoryBottomSheet();
    }

    public static AddEditCategoryBottomSheet newEditInstance(Category category) {
        AddEditCategoryBottomSheet sheet = new AddEditCategoryBottomSheet();
        sheet.category = category;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_edit_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        TextView tvTitle = view.findViewById(R.id.tvCategorySheetTitle);
        editName = view.findViewById(R.id.editCategoryName);
        buttonExpense = view.findViewById(R.id.buttonCategoryExpense);
        buttonIncome = view.findViewById(R.id.buttonCategoryIncome);
        tvIconPreview = view.findViewById(R.id.tvCategoryIconPreview);
        tvCategoryIconName = view.findViewById(R.id.tvCategoryIconName);
        viewCategoryColorPreview = view.findViewById(R.id.viewCategoryColorPreview);
        tvCategoryColorName = view.findViewById(R.id.tvCategoryColorName);

        if (category == null) {
            tvTitle.setText("Thêm danh mục");
        } else {
            tvTitle.setText("Sửa danh mục");
            editName.setText(category.getName());
            selectedType = category.getType();
            selectedIcon = category.getIcon();
            selectedColor = category.getColor();
        }

        updateType();
        updateIconPreview();
        updateColorPreview();

        view.findViewById(R.id.buttonCloseCategorySheet)
                .setOnClickListener(v -> dismiss());

        buttonExpense.setOnClickListener(v -> {
            selectedType = 2;
            updateType();
        });

        buttonIncome.setOnClickListener(v -> {
            selectedType = 1;
            updateType();
        });

        view.findViewById(R.id.rowCategoryIcon)
                .setOnClickListener(v -> showIconSelector());

        view.findViewById(R.id.rowCategoryColor)
                .setOnClickListener(v -> showColorSelector());

        view.findViewById(R.id.buttonSaveCategory)
                .setOnClickListener(v -> saveCategory());

        observeAction();
    }

    private void updateType() {
        if (selectedType == 1) {
            buttonIncome.setBackgroundResource(
                    R.drawable.bg_transaction_type_income_selected);

            buttonIncome.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.income_blue));

            buttonIncome.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD);

            buttonExpense.setBackgroundResource(
                    R.drawable.bg_transaction_type_unselected);

            buttonExpense.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.light_text_secondary));

            buttonExpense.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL);
        } else {
            buttonExpense.setBackgroundResource(
                    R.drawable.bg_transaction_type_expense_selected);

            buttonExpense.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.expense_red));

            buttonExpense.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD);

            buttonIncome.setBackgroundResource(
                    R.drawable.bg_transaction_type_unselected);

            buttonIncome.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.light_text_secondary));

            buttonIncome.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL);
        }
    }

    private void showIconSelector() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(24));

        TextView title = new TextView(requireContext());
        title.setText("Chọn biểu tượng");
        title.setTextSize(18);
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_text_primary));
        title.setGravity(Gravity.CENTER);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(48)));

        String[] icons = {
                "restaurant", "local_cafe", "fastfood", "shopping_cart",
                "shopping_bag", "directions_car", "directions_bus",
                "local_gas_station", "home", "lightbulb", "phone_android",
                "laptop", "sports_esports", "movie", "music_note",
                "sports_soccer", "school", "menu_book", "medical_services",
                "local_pharmacy", "pets", "card_giftcard", "payments",
                "account_balance"
        };

        LinearLayout row = null;

        for (int i = 0; i < icons.length; i++) {
            if (i % 4 == 0) {
                row = new LinearLayout(requireContext());
                row.setGravity(Gravity.CENTER);
                row.setOrientation(LinearLayout.HORIZONTAL);

                root.addView(
                        row,
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                dp(68)));
            }

            ImageView icon = new ImageView(requireContext());
            icon.setImageResource(
                    IconResolver.getIconResId(icons[i]));
            icon.setColorFilter(Color.DKGRAY);
            icon.setPadding(
                    dp(12),
                    dp(12),
                    dp(12),
                    dp(12));
            icon.setContentDescription(
                    getIconDisplayName(icons[i]));

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(60),
                            1);

            params.setMargins(
                    dp(4),
                    dp(4),
                    dp(4),
                    dp(4));

            row.addView(icon, params);

            final String value = icons[i];

            icon.setOnClickListener(v -> {
                selectedIcon = value;
                updateIconPreview();
                dialog.dismiss();
            });
        }

        dialog.setContentView(root);
        dialog.show();
    }

    private void showColorSelector() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(24));

        TextView title = new TextView(requireContext());
        title.setText("Chọn màu");
        title.setTextSize(18);
        title.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.light_text_primary));
        title.setGravity(Gravity.CENTER);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(48)));

        String[] colors = {
                "#43A047", "#1E88E5", "#8E24AA", "#E53935",
                "#FB8C00", "#00897B", "#6D4C41", "#546E7A"
        };

        LinearLayout row = null;

        for (int i = 0; i < colors.length; i++) {
            if (i % 4 == 0) {
                row = new LinearLayout(requireContext());
                row.setGravity(Gravity.CENTER);
                row.setOrientation(LinearLayout.HORIZONTAL);

                root.addView(
                        row,
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                dp(72)));
            }

            TextView colorView = new TextView(requireContext());
            colorView.setGravity(Gravity.CENTER);
            colorView.setText(
                    colors[i].equalsIgnoreCase(selectedColor)
                            ? "✓"
                            : "");
            colorView.setTextColor(Color.WHITE);
            colorView.setTextSize(20);
            colorView.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD);

            colorView.setBackgroundResource(
                    R.drawable.bg_transaction_icon);

            colorView.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor(colors[i])));

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            dp(52),
                            dp(52));

            params.setMargins(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(8));

            row.addView(colorView, params);

            final String value = colors[i];

            colorView.setOnClickListener(v -> {
                selectedColor = value;
                updateColorPreview();
                updateIconPreview();
                dialog.dismiss();
            });
        }

        dialog.setContentView(root);
        dialog.show();
    }

    private void updateIconPreview() {
        tvIconPreview.setBackgroundResource(
                R.drawable.bg_transaction_icon);

        tvIconPreview.setBackgroundTintList(
                ColorStateList.valueOf(
                        parseColor(selectedColor)));

        tvIconPreview.setText("");

        tvIconPreview.setCompoundDrawablesWithIntrinsicBounds(
                IconResolver.getIconResId(selectedIcon),
                0,
                0,
                0);

        tvIconPreview.setCompoundDrawableTintList(
                ColorStateList.valueOf(Color.WHITE));

        tvCategoryIconName.setText(
                getIconDisplayName(selectedIcon));
    }

    private void updateColorPreview() {
        viewCategoryColorPreview.setBackgroundResource(
                R.drawable.bg_transaction_icon);

        viewCategoryColorPreview.setBackgroundTintList(
                ColorStateList.valueOf(
                        parseColor(selectedColor)));

        tvCategoryColorName.setText(
                getColorDisplayName(selectedColor));
    }

    private void observeAction() {
        viewModel.getCategoryAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case LOADING:
                            setActionEnabled(false);
                            break;

                        case SUCCESS:
                            setActionEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    category == null
                                            ? "Đã thêm danh mục."
                                            : "Đã cập nhật danh mục.",
                                    Toast.LENGTH_SHORT).show();

                            sendCategoryChangedResult();
                            viewModel.resetCategoryAction();
                            dismiss();
                            break;

                        case ERROR:
                            setActionEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    resource.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case IDLE:
                        default:
                            setActionEnabled(true);
                            break;
                    }
                });

        viewModel.getDeleteAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case LOADING:
                            setActionEnabled(false);
                            break;

                        case SUCCESS:
                        case ERROR:
                            setActionEnabled(true);
                            break;

                        case IDLE:
                        default:
                            setActionEnabled(true);
                            break;
                    }
                });
    }

    private void sendCategoryChangedResult() {
        Bundle result = new Bundle();
        result.putBoolean(
                CATEGORY_CHANGED_BUNDLE,
                true);

        getParentFragmentManager().setFragmentResult(
                CATEGORY_CHANGED_RESULT,
                result);
    }

    private void setActionEnabled(boolean enabled) {
        requireView()
                .findViewById(R.id.buttonSaveCategory)
                .setEnabled(enabled);
    }

    private void saveCategory() {
        String name = editName.getText() == null
                ? ""
                : editName.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError(
                    "Vui lòng nhập tên danh mục.");
            editName.requestFocus();
            return;
        }

        if (category == null) {
            viewModel.createCategory(
                    name,
                    selectedType,
                    selectedIcon,
                    selectedColor);
            return;
        }

        viewModel.updateCategory(
                category.getId(),
                name,
                selectedType,
                selectedIcon,
                selectedColor);
    }

    private String getIconDisplayName(String icon) {
        switch (icon) {
            case "restaurant": return "Nhà hàng";
            case "local_cafe": return "Cà phê";
            case "fastfood": return "Đồ ăn nhanh";
            case "shopping_cart": return "Mua sắm";
            case "shopping_bag": return "Túi mua sắm";
            case "directions_car": return "Ô tô";
            case "directions_bus": return "Xe buýt";
            case "local_gas_station": return "Xăng xe";
            case "home": return "Nhà ở";
            case "lightbulb": return "Điện";
            case "phone_android": return "Điện thoại";
            case "laptop": return "Máy tính";
            case "sports_esports": return "Trò chơi";
            case "movie": return "Phim";
            case "music_note": return "Âm nhạc";
            case "sports_soccer": return "Thể thao";
            case "school": return "Học tập";
            case "menu_book": return "Sách";
            case "medical_services": return "Y tế";
            case "local_pharmacy": return "Thuốc";
            case "pets": return "Thú cưng";
            case "card_giftcard": return "Quà tặng";
            case "payments": return "Thanh toán";
            case "account_balance": return "Ngân hàng";
            default: return "Biểu tượng";
        }
    }

    private String getColorDisplayName(String color) {
        switch (color) {
            case "#43A047": return "Xanh lá";
            case "#1E88E5": return "Xanh dương";
            case "#8E24AA": return "Tím";
            case "#E53935": return "Đỏ";
            case "#FB8C00": return "Cam";
            case "#00897B": return "Xanh teal";
            case "#6D4C41": return "Nâu";
            case "#546E7A": return "Xám xanh";
            default: return "Màu đã chọn";
        }
    }

    private int parseColor(String value) {
        try {
            return Color.parseColor(value);
        } catch (Exception ignored) {
            return ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_green);
        }
    }

    private int dp(int value) {
        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density +
                        0.5f);
    }
}