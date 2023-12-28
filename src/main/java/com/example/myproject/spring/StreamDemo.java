package com.example.myproject.spring;

import com.example.myproject.bean.Student;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 简单demo
 */
public class StreamDemo {

    // 求和
    public static void main11(String[] args) {
        List<String> a = Lists.newArrayList("1","0","3","4");
        BigDecimal flCount = a.stream().map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        System.out.println(flCount);
    }

    // 多个List的整合处理
    public static void main22(String[] args) {
        List<Integer> a = new ArrayList<Integer>(java.util.Arrays.asList(1,2,3,4,5,6));
        List<Integer> b = new ArrayList<Integer>(java.util.Arrays.asList(7,8,9));
        List<List<Integer>> c = new ArrayList<>(java.util.Arrays.asList(a,b));
        System.out.println(c);
        List<Integer> d = c.stream().flatMap(Collection::stream).collect(Collectors.toList());
        System.out.println(d);
    }

    public static void main(String[] args) {
        // 一个集合中放入4个学生对象
        List<Student> list = new ArrayList<>();
        list.add(new Student(10002L, "ZhangSan"));
        list.add(new Student(10001L, "LiSi"));
        list.add(new Student(10004L, "Peter"));
        list.add(new Student(10004L, "Peter"));

        // 指定某个字段去重
        System.out.println("指定age属性去重（方法一）：");
        list.stream().filter(distinctByKey1(s -> s.getNum()))
                .forEach(System.out::println);

        // 方法二（用循环)
        System.out.println("指定age属性去重（方法二）：");
        TreeSet<Student> students = new TreeSet<>(Comparator.comparing(Student::getNum).thenComparing(Student::getName));
        for (Student student : list) {
            students.add(student);
        }
        students.forEach(System.out::println);

        // 方法三：（是方法二的变形）
        System.out.println("指定age属性去重（方法三）：");
        list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(s -> s.getNum()))), ArrayList::new))
                .forEach(System.out::println);
    }

    static <T> Predicate<T> distinctByKey1(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        //如果不存在（新的entry），那么会向map中添加该键值对，并返回null。
        //如果已经存在，那么不会覆盖已有的值，直接返回已经存在的值
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
