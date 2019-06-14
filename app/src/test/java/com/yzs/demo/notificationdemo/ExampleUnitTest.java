package com.yzs.demo.notificationdemo;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Queue<String> queue = new LinkedList<>();
        queue.offer("yang1");
        queue.offer("yang2");
        queue.offer("yang3");
        queue.offer("yang4");
        System.out.print(queue.toString());
        System.out.print("===================");
        while (queue.size() > 0) {


            System.out.print(queue.poll());
        }

    }
}