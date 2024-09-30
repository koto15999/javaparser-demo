package main;

public class Sample {

    public static void main(String[] args) {
        Context context = new Context();
        context.call("Xcad1pq.class", "abc");
        context.call("Xcad2pq.class", "123");
        context.call("Xcad3pq.class", "xyz"); 
    }
}

class Context {
    public void call(String className, Object arg) {
        System.out.println("classname: " + className + " with argument: " + arg);
    }
}
