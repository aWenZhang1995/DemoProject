package com.example.myproject.spring;

import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AsyncDemo {
    public static void main(String[] args) {
        runAsync();
    }

    //无返回值
    @SneakyThrows
    public static void runAsync(){
        // CompletableFuture 提供了四个静态方法来创建一个异步操作。
        CompletableFuture<Void> b1 = CompletableFuture.runAsync(()-> System.out.println("b1"));
        CompletableFuture<String> b3 = CompletableFuture.supplyAsync(() -> {
            fail("ex");
            return "b3";
        });
        CompletableFuture<String> b4 = CompletableFuture.supplyAsync(() -> "b4");

        // 当CompletableFuture的计算结果完成，或者抛出异常的时候，可以执行特定的Action。主要是下面方法
        // a为上次计算的结果 b为抛出的异常
        CompletableFuture<String> c1 = b4.whenComplete((a,b) -> System.out.println("c1"));
        CompletableFuture<String> c4 = b3.exceptionally((b) -> "error");
        System.out.println(c4.get());

        // 当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化。 与whenComplete区别是
        // thenApply返回结果可以再加工whenComplete返回结果不会再加工
        CompletableFuture<String> d1 = b4.thenApply((a) -> "d1");
        CompletableFuture<String> d2 = b4.thenApplyAsync((a) -> "d2");

        // handle是执行任务完成时对结果的处理。 和 thenApply 方法处理方式基本一样。不同的是 handle 是在任务完成后再执行，还可以处理异常的任务。
        // thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法
        CompletableFuture<String> e1 = b4.handle((a,b) -> "e1");
        CompletableFuture<String> e2 = b4.handleAsync((a,b) -> "e2");
        CompletableFuture<String> e3 = b3.handleAsync((a,b) -> null ==b ? "e3" : "e33333",(r)-> r.run());

        // thenAccept 消费处理结果 接收任务的处理结果，并消费处理，无返回结果。
        CompletableFuture<Void> f1 = e3.thenAccept((a)-> System.out.println("f1"));
        CompletableFuture<Void> f2 = e3.thenAcceptAsync((a)-> System.out.println("f1"));
        CompletableFuture<Void> f3 = e3.thenAcceptAsync((a)-> System.out.println("f1"),(r)-> r.run());

        // thenRun 方法与thenAccept 方法不一样的是，不关心任务的处理结果。只要上面的任务执行完成，就开始执行 thenAccept 。
        CompletableFuture<Void> g1 = e3.thenRun(()-> System.out.println("f1"));
        CompletableFuture<Void> f22 = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("22222");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).whenComplete((a,b) -> {
            System.out.println(a.TYPE);
            System.out.println("222---");
        });
        CompletableFuture<String> f33 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("33333");
                fail("000000");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "f3 success";
        }).exceptionally(e -> "f3 fail");
        CompletableFuture<Boolean> f4 = f33.thenApplyAsync(a->{
            System.out.println(a);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(a.contains("success")){
                System.out.println("f4 success");
                return true;
            }
            System.out.println("f4 fail");
            return false;
        });
        f3.get();
        String dd = f33.getNow("dd");
        System.out.println(dd);
    }
}
