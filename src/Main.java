import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;
// ------------ ********  Java 8 streams can’t be reused. ----------- ****


public class Main {
    Main(){

    }

    private static <T> void streamUsingGenerate(T val){
        Stream<T> stream=  Stream.generate(()->val).limit(10);
        stream.forEach(System.out::println);
    }

    private void streamUsingIterate(Integer initialVal){
        Stream<Integer> stream= Stream.iterate(initialVal,n->n+2).limit(10);
        stream.forEach(val->System.out.print(val+ " "));
    }


    /**
     * notes
     * The API has many terminal operations which aggregate a stream to a type or to a primitive: count(), max(), min(), and sum(). However, these operations work according to the predefined implementation. So what if a developer needs to customize a Stream’s reduction mechanism? There are two methods which allow us to do this, the reduce() and the collect() methods.
     */


    private void reduceFunction(){
        /**
         *
         * to make combiner work the accumulator should perform in a parallelStream so that the combiner can combine accumulator each result and reduce to a final result
         */
          int reducedParallel = Arrays.asList(1, 2, 3).parallelStream()
              .reduce(10, Integer::sum, (a, b) -> {
                System.out.println("combiner was called");
               return a + b;
              });

              // answer will be 10+1=11 , 10+2=12 ,10+3=13  and then combiner will combine this three parallel result and reduce the final answer as 12+13=25 -> 25+11=36

    }


    /**
     * Processing the average value of all numeric elements of the stream:
     *
     * double averagePrice = productList.stream()
     *   .collect(Collectors.averagingInt(Product::getPrice));
     * Copy
     * Processing the sum of all numeric elements of the stream:
     *
     * int summingPrice = productList.stream()
     *   .collect(Collectors.summingInt(Product::getPrice));
     *
     * The methods averagingXX(), summingXX() and summarizingXX() can work with primitives (int, long, double) and with their wrapper classes (Integer, Long, Double). One more powerful feature of these methods is providing the mapping. As a result, the developer doesn’t need to use an additional map() operation before the collect() method.
     *
     */


    /**
     * boxed() is used to convert a primitive stream into a Stream of wrapper objects.
     *
     * Primitive Streams in Java
     *
     * IntStream
     *
     * LongStream
     *
     * DoubleStream
     *
     * These work with primitives (int, long, double) — not objects.
     *
     */
    public static void main(String[] args) {
// creation of streams using generator
//        streamUsingGenerate("element");  // output : element element ,element  --- 10 times

Main main= new Main();
//main.streamUsingIterate(2); // output : 2 4 6 8 10 12 14 16 18 20

        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        Stream<String> size = list.stream().skip(1)
                .map(element -> element.substring(0, 3)).sorted();

        size.forEach(System.out::print);


        // using collect method to reduce the stream
        List<Product> productStream= List.of(
                new Product(25,"potatoes"),
                new Product(30,"orange"),
                new Product(20,"lemon"),
                new Product(20,"bread")
        );

        String listToString = productStream.stream().map(Product::getName)
                .collect(Collectors.joining(", ", "[", "]"));
//        System.out.println(listToString); // output : [potatoes, orange, lemon, bread]

        double avgPrice= productStream.stream().collect(Collectors.averagingDouble(Product::getPrice));
//        System.out.print(avgPrice);// output :  23.75


        // Grouping
        Map<Integer,List<Product>> productGroupedByPrice = productStream.stream().collect(Collectors.groupingBy(Product::getPrice));
//        System.out.println(productGroupedByPrice.toString());

        // grouping based on a predicate or condition
        Map<Boolean, List<Product>> partitionedByGreaterThenAvg = productStream.stream()
                .collect(Collectors.partitioningBy(element -> element.getPrice() > avgPrice));
//        System.out.println(partitionedByGreaterThenAvg.toString());


//        Used when each element produces multiple elements.
        // flattens the inner nested loop and access the element and perform operation on that
        List<List<Integer>> flatList = List.of(
                List.of(1,2),
                List.of(3,4)
        );

        flatList.stream()
                .flatMap(List::stream)
                .forEach(System.out::println);


//        Q1 : find duplicate elements in a list using streams
        List<Integer> question= List.of(1,2,1,2,1,3,4,5);
        // to find the duplicate element

        Set<Integer> duplicates= question.stream()
                .collect(Collectors.groupingBy(i->i, Collectors.counting()))
                .entrySet().stream().filter(entry->entry.getValue()>1).map(Map.Entry::getKey).collect(Collectors.toSet());
        System.out.println(duplicates.toString()); // output : [1, 2]



        //Q2 : Find first non-repeating character in a String
        String Q2= "swiss";
        Optional<Map.Entry<Character,Long>> ch= Q2.chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(i->i,LinkedHashMap:: new,Collectors.counting())).entrySet().stream()
                .filter(entry->entry.getValue()==1).findFirst();
        ch.ifPresent(entry->System.out.println(entry.getKey()));  // output : w

        // Q3  :Sort employees by salary (ascending & descending)
        List<Employee> employees = Arrays.asList(
                new Employee(101, "Amit", 75000, "IT"),
                new Employee(102, "Priya", 82000, "HR"),
                new Employee(103, "Rahul", 68000, "Finance"),
                new Employee(104, "Sneha", 82000, "IT"),     // duplicate salary
                new Employee(105, "Vikram", 95000, "Management"),
                new Employee(106, "Neha", 72000, "HR"),
                new Employee(107, "Arjun", 68000, "IT"),     // duplicate salary
                new Employee(108, "Kavya", 88000, "Finance")
        );
        List<Employee> sortedEmp= employees.stream().sorted(Comparator.comparingDouble(Employee::getSalary)).toList();
        System.out.println(sortedEmp.toString()); // output : [103 | Rahul | 68000.0 | Finance, 107 | Arjun | 68000.0 | IT, 106 | Neha | 72000.0 | HR, 101 | Amit | 75000.0 | IT, 102 | Priya | 82000.0 | HR, 104 | Sneha | 82000.0 | IT, 108 | Kavya | 88000.0 | Finance, 105 | Vikram | 95000.0 | Management]

        // Q4 :  Group employees by department
        Map<String,List<Employee>> departmentEmp= employees.stream().collect(Collectors.groupingBy(Employee::getDepartment));
        System.out.println(departmentEmp.toString());
        // output :  {Finance=[103 | Rahul | 68000.0 | Finance, 108 | Kavya | 88000.0 | Finance], HR=[102 | Priya | 82000.0 | HR, 106 | Neha | 72000.0 | HR], Management=[105 | Vikram | 95000.0 | Management], IT=[101 | Amit | 75000.0 | IT, 104 | Sneha | 82000.0 | IT, 107 | Arjun | 68000.0 | IT]}

// Q5:   Find second Highest number in a list using the list provided in Q1
        Integer secondHighest= (Integer)question.stream().distinct().sorted().toArray()[1];
        System.out.println(secondHighest); // output : 2


        // Q6 : Partition numbers into even and odd
        Map<Boolean,List<Integer>> mapForEven_Odd=question.stream().distinct().collect(Collectors.partitioningBy(e->e%2==0));
        System.out.println(mapForEven_Odd.toString());

        //Q6 : Find longest string in a list --> approach sort the string based on the length and return the findFirst



    }
}

class Product{
    String name;
    int price;

    public Product(int price, String name) {
        this.price = price;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

class Employee {
    private int id;
    private String name;
    private double salary;
    private String department;

    public Employee(int id, String name, double salary, String department) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getSalary() { return salary; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + salary + " | " + department;
    }
}