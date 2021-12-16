package com.tyron.code.ui.file.action.file;

import android.content.DialogInterface;
import android.text.Editable;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;
import com.tyron.code.R;
import com.tyron.code.ui.component.tree.TreeNode;
import com.tyron.code.ui.file.action.ActionContext;
import com.tyron.code.ui.file.action.FileAction;
import com.tyron.code.ui.file.tree.TreeUtil;
import com.tyron.code.ui.file.tree.model.TreeFile;
import com.tyron.common.util.SingleTextWatcher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CreateFileAction extends FileAction {
    @Override
    public boolean isApplicable(File file) {
        return file.isDirectory();
    }

    @Override
    public void addMenu(ActionContext context) {
        SubMenu subMenu = context.addSubMenu("new",
                context.getFragment().getString(R.string.menu_new));
        subMenu.add(R.string.menu_action_new_file)
                .setOnMenuItemClickListener(i -> onMenuItemClick(context));
    }

    @SuppressWarnings("ConstantConditions")
    private boolean onMenuItemClick(ActionContext context) {
        File currentDir = context.getCurrentNode().getValue().getFile();
        AlertDialog dialog = new AlertDialog.Builder(context.getFragment().requireContext())
                .setView(R.layout.create_class_dialog)
                .setTitle(R.string.menu_action_new_file)
                .setPositiveButton(R.string.create_class_dialog_positive, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener((d) -> {
            dialog.findViewById(R.id.til_class_type).setVisibility(View.GONE);
            TextInputLayout til = dialog.findViewById(R.id.til_class_name);
            EditText editText = dialog.findViewById(R.id.et_class_name);
            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

            til.setHint(R.string.file_name);
            positive.setOnClickListener(v -> {
                File fileToCreate = new File(currentDir, editText.getText().toString());
                if (!createFileSilently(fileToCreate)) {
                    new AlertDialog.Builder(context.getFragment().requireContext())
                            .setTitle(R.string.error)
                            .setMessage(R.string.error_dir_access)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {
                    refreshTreeView(context);
                    dialog.dismiss();
                }
            });
            editText.addTextChangedListener(new SingleTextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    File file = new File(currentDir, editable.toString());
                    positive.setEnabled(!file.exists());
                }
            });
        });
        dialog.show();
        return true;
    }

    private boolean createFileSilently(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private void refreshTreeView(ActionContext context) {
        TreeNode<TreeFile> currentNode = context.getCurrentNode();
        TreeUtil.updateNode(currentNode);
        context.getTreeView().refreshTreeView();
    }
}