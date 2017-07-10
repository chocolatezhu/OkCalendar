package com.ppzhu.calendar.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;

import java.lang.reflect.Method;

/**
 * @author zzl
 * 使用系统通用弹框
 * Created on 2016/8/24.
 */
public class CustomAlertDialog {
    private final static int PRIVATE_FLAG_PREVENT_DIALOG_CENTER = 0x08000000;
    private final static int PRIVATE_FLAG_PREVENT_DIALOG_NO_ANIMATE = 0x10000000;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ImplementCustomAlertDialogListener customAlertDialogListener;

    public CustomAlertDialog( Context context, ImplementCustomAlertDialogListener listener ) {
        this( context, AlertDialog.THEME_HOLO_LIGHT , listener );
    }

    public CustomAlertDialog( Context context, int theme, ImplementCustomAlertDialogListener listener ) {
        builder = new AlertDialog.Builder( context, theme );
        this.customAlertDialogListener = listener;
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle( CharSequence title ) {
        builder.setTitle( title );
    }

    /**
     * 设置标题
     * @param titleId
     */
    public void setTitle( int titleId ) {
        builder.setTitle( titleId );
    }

    /**
     * 设置内容
     * @param message
     */
    public void setMessage( CharSequence message ) {
        builder.setMessage(message);
    }

    /**
     * 设置内容
     * @param messageId
     */
    public void setMessage( int messageId ) {
        builder.setMessage( messageId );
    }

    /**
     * 设置图标
     * @param icon
     */
    public void setIcon( Drawable icon ) {
        builder.setIcon(icon);
    }

    /**
     * 设置图标
     * @param iconId
     */
    public void setIcon( int iconId ) {
        builder.setIcon( iconId );
    }

    /**
     * 设置Positive按钮
     * @param text
     */
    public void setPositiveButton( CharSequence text ) {
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onPositiveButton();
                }
            }
        });
    }

    /**
     * 设置Positive按钮
     * @param textId
     */
    public void setPositiveButton( int textId ) {
        builder.setPositiveButton( textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ( null != customAlertDialogListener ) {
                    customAlertDialogListener.onPositiveButton();
                }
            }
        } );
    }

    /**
     * 设置Neutral按钮
     * @param text
     */
    public void setNeutralButton( CharSequence text ) {
        builder.setNeutralButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onNeutralButton();
                }
            }
        });
    }

    /**
     * 设置Neutral按钮
     * @param textId
     */
    public void setNeutralButton( int textId ) {
        builder.setNeutralButton( textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ( null != customAlertDialogListener ) {
                    customAlertDialogListener.onNeutralButton();
                }
            }
        } );
    }

    /**
     * 设置Negative按钮
     * @param text
     */
    public void setNegativeButton( CharSequence text ) {
        builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onNegativeButton();
                }
            }
        });
    }

    /**
     * 设置Negative按钮
     * @param textId
     */
    public void setNegativeButton( int textId ) {
        builder.setNegativeButton( textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ( null != customAlertDialogListener ) {
                    customAlertDialogListener.onNegativeButton();
                }
            }
        } );
    }

    /**
     * 设置列表item的选项
     * @param items
     */
    public void setItems( CharSequence[] items ) {
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onItemClick(i);
                }
            }
        });
    }

    /**
     * 设置列表item的选项
     * @param adapter
     */
    public void setAdapter( ListAdapter adapter ) {
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onItemClick(i);
                }
            }
        });
    }

    /**
     * 设置单选列表选项和监听
     * @param items
     * @param checkedItem
     */
    public void setSingleChoiceItems( CharSequence[] items, int checkedItem ) {
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onSingleChoiceItemClick(i);
                }
            }
        });
    }

    /**
     * 设置单选列表选项和监听
     * @param adapter
     * @param checkedItem
     */
    public void setSingleChoiceItems( ListAdapter adapter, int checkedItem ) {
        builder.setSingleChoiceItems( adapter, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ( null != customAlertDialogListener ) {
                    customAlertDialogListener.onSingleChoiceItemClick( i );
                }
            }
        });
    }

    /**
     * 设置多选列表选项和监听
     * @param items
     * @param checkedItems
     */
    public void setMultiChoiceItems( CharSequence[] items, boolean[] checkedItems ) {
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onMultiChoiceItemClick(i, b);
                }
            }
        });
    }

    /**
     * 创建dialog
     */
    private void createAlertDialog() {
        if ( null == alertDialog ) {
            alertDialog = builder.create();
        }
    }

    /**
     * 显示弹出框,默认按照系统样式弹出
     */
    public void show() {
        createAlertDialog();
        alertDialog.show();
    }

    /**
     * 显示弹出框,中间弹出
     */
    public void centerShow() {
        createAlertDialog();
        setDialogCenter(alertDialog.getWindow());
        alertDialog.show();
    }

    /**
     * 显示弹出框,无动画弹出
     */
    public void noneShow() {
        createAlertDialog();
        setDialogNoAnimation(alertDialog.getWindow());
        alertDialog.show();
    }

    private void setDialogNoAnimation( Window window ) {
        try {
            Method method = Window.class.getDeclaredMethod( "setPrivateFlags", int.class, int.class );
            method.setAccessible( true );
            method.invoke( window, PRIVATE_FLAG_PREVENT_DIALOG_NO_ANIMATE, PRIVATE_FLAG_PREVENT_DIALOG_NO_ANIMATE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 设置全局弹框
     * */
    public void setShowSystemAlert(){
        if (null == alertDialog){
            return;
        }
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    /**
     * 消失弹出框
     */
    public void dismiss() {
        createAlertDialog();
        alertDialog.dismiss();
    }

    /**
     * 是否显示
     * @return
     */
    public boolean isShowing() {
        createAlertDialog();
        return alertDialog.isShowing();
    }

    public void setCancelable( boolean cancelable ) {
        createAlertDialog();
        alertDialog.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside( boolean cancel ) {
        createAlertDialog();
        alertDialog.setCanceledOnTouchOutside(cancel);
    }

    /**
     * 设置取消监听
     */
    public void setOnCancelListener() {
        createAlertDialog();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onCancel();
                }
            }
        });
    }

    /**
     * 设置取消监听
     */
    public void setOnKeyListener() {
        createAlertDialog();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (null != customAlertDialogListener) {
                    return customAlertDialogListener.onKey(i, keyEvent);
                }
                return false;
            }
        });
    }

    /**
     * 设置消失监听
     */
    public void setOnDismissListener() {
        createAlertDialog();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (null != customAlertDialogListener) {
                    customAlertDialogListener.onDismiss();
                }
            }
        });
    }

    /**
     * 可以在message下增加一个自定义view效果
     * @param view
     */
    public void setView( View view ) {
        createAlertDialog();
        alertDialog.setView(view);
    }

    public void setContentView(View view) {
        createAlertDialog();
        alertDialog.setContentView(view);
    }

    /**
     *  设置弹框居中显示
     * */
    public static void setDialogCenter( Window window ) {
        try {
            Method method = Window.class.getDeclaredMethod( "setPrivateFlags", int.class, int.class );
            method.setAccessible( true );
            method.invoke( window, PRIVATE_FLAG_PREVENT_DIALOG_CENTER, PRIVATE_FLAG_PREVENT_DIALOG_CENTER );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
