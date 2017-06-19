package com.ranosys.andropaint.algorithm;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.ranosys.andropaint.callbacks.FloodFillCallback;

import java.util.LinkedList;
import java.util.Queue;

 public class FloodFill  {
    public void floodFill(Bitmap image, Point node, int targetColor,
                          int replacementColor) {
        int width = image.getWidth();
        int height = image.getHeight();
        int target = targetColor;
        int replacement = replacementColor;
        if (target != replacement) {
            Queue<Point> queue = new LinkedList<Point>();
            do {
                int x = node.x;
                int y = node.y;
                while (x > 0 && image.getPixel(x - 1, y) == target) {
                    x--;
                }
                boolean spanUp = false;
                boolean spanDown = false;
                while (x < width && image.getPixel(x, y) == target) {
                    image.setPixel(x, y, replacement);
                    if (!spanUp && y > 0 && image.getPixel(x, y - 1) == target) {
                        queue.add(new Point(x, y - 1));
                        spanUp = true;
                    } else if (spanUp && y > 0
                            && image.getPixel(x, y - 1) != target) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1
                            && image.getPixel(x, y + 1) == target) {
                        queue.add(new Point(x, y + 1));
                        spanDown = true;
                    } else if (spanDown && y < height - 1
                            && image.getPixel(x, y + 1) != target) {
                        spanDown = false;
                    }
                    x++;
                }
            } while ((node = queue.poll()) != null);
        }
    }


     public void floodFillNew(Bitmap bmp, Point pt, int targetColor, int replacementColor)
     {
         Queue<Point> q = new LinkedList<Point>();
         q.add(pt);
         while (q.size() > 0) {
             Point n = q.poll();
             if (bmp.getPixel(n.x, n.y) != targetColor)
                 continue;

             Point w = n, e = new Point(n.x + 1, n.y);
             while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                 bmp.setPixel(w.x, w.y, replacementColor);
                 if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                     q.add(new Point(w.x, w.y - 1));
                 if ((w.y < bmp.getHeight() - 1)
                         && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                     q.add(new Point(w.x, w.y + 1));
                 w.x--;
             }
             while ((e.x < bmp.getWidth() - 1)
                     && (bmp.getPixel(e.x, e.y) == targetColor)) {
                 bmp.setPixel(e.x, e.y, replacementColor);

                 if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                     q.add(new Point(e.x, e.y - 1));
                 if ((e.y < bmp.getHeight() - 1)
                         && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                     q.add(new Point(e.x, e.y + 1));
                 e.x++;
             }
         }
     }
}