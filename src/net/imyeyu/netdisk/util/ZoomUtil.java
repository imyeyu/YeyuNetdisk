package net.imyeyu.netdisk.util;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ZoomUtil {
	
    private static boolean isRight; // 是否处于右边界调整窗口状态
    private static boolean isBottomRight; // 是否处于右下角调整窗口状态
    private static boolean isBottom; // 是否处于下边界调整窗口状态
    private final static int RESIZE_WIDTH = 10; // 判定是否为调整窗口状态的范围与边界距离
    
    public static void addDragEvent(Stage stage, Node root, double minWidth, double minHeight) {
        root.setOnMouseMoved((MouseEvent event) -> {
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Cursor cursorType = Cursor.DEFAULT;
            // 先将所有调整窗口状态重置
            isRight = isBottomRight = isBottom = false;
            if (y >= height - RESIZE_WIDTH) {
                if (x <= RESIZE_WIDTH) { // 左下角调整窗口状态

                } else if (x >= width - RESIZE_WIDTH) { // 右下角调整窗口状态
                    isBottomRight = true;
                    cursorType = Cursor.SE_RESIZE;
                } else { // 下边界调整窗口状态
                    isBottom = true;
                    cursorType = Cursor.S_RESIZE;
                }
            } else if (x >= width - RESIZE_WIDTH) { // 右边界调整窗口状态
                isRight = true;
                cursorType = Cursor.E_RESIZE;
            }
            // 最后改变鼠标光标
            root.setCursor(cursorType);
        });

        root.setOnMouseDragged((MouseEvent event) -> {
            double x = event.getSceneX();
            double y = event.getSceneY();
            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = stage.getX();
            double nextY = stage.getY();
            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();
            if (isRight || isBottomRight) { // 所有右边调整窗口状态
                nextWidth = x;
            }
            if (isBottomRight || isBottom) { // 所有下边调整窗口状态
                nextHeight = y;
            }
            if (nextWidth <= minWidth) { // 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                nextWidth = minWidth;
            }
            if (nextHeight <= minHeight) { // 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
                nextHeight = minHeight;
            }
            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            stage.setX(nextX);
            stage.setY(nextY);
            stage.setWidth(nextWidth);
            stage.setHeight(nextHeight);
        });
    }
}