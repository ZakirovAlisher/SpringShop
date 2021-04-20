package kz.springboot.springbootdemo.db;

import java.sql.Date;
import java.util.ArrayList;

public class DBManager {
    private static ArrayList<Tasks> ta = new ArrayList<>();
    private static ArrayList<Shopitem> items = new ArrayList<>();
static {
//    Long id;
//    String name;
//    String description;
//    int price;
//    int amount;
//    int stars; // Just rating, from 0 to 5
//    String pictureUrl;


    items.add(new Shopitem(1L, "name", "desc", 100 , 2, 1,"https://picsum.photos/id/22/200/300"));
    items.add(new Shopitem(2L, "name2", "desc", 100 , 2, 5,"https://picsum.photos/id/1/200/300"));
    items.add(new Shopitem(3L, "name3", "desc", 100 , 2, 5,"https://picsum.photos/id/999/200/300"));
}
private  static Long id = 4L;
    public  static  ArrayList<Shopitem> getItems(){
        return items;
    }

    public  static  void addItem(Shopitem t){
        t.setId(id);
        items.add(t);
        id++;
    }





    public  static  Shopitem getItem(Long t){
        for (Shopitem tt: items
        ) {
            if(tt.getId() == t) return tt;

        }
        return null;
    }
    public  static  void delItem(Long t){
        for (Shopitem tt: items
        ) {
            if(tt.getId() == t) {
                items.remove(tt);
                return ;}

        }

    }


public  static  ArrayList<Tasks> getTasks(){
    return ta;
}

    public  static  void addTask(Tasks t){
    t.setId(id);
    ta.add(t);
    id++;
    }





    public  static  Tasks getTask(Long t){
        for (Tasks tt: ta
             ) {
            if(tt.getId() == t) return tt;

        }
        return null;
    }
    public  static  void delTask(Long t){
        for (Tasks tt: ta
        ) {
            if(tt.getId() == t) {
                items.remove(tt);
                return ;}

        }

    }

    public  static  void saveTask(Tasks t){
        for (Tasks tt: ta
        ) {
            if(tt.getId() == t.getId()) {

                tt.setName(t.getName());
                tt.setDescription(t.getDescription());
                tt.setDeadlineDate(t.getDeadlineDate());
                tt.setCompleted(t.isCompleted());
                return;
            }
        }

    }


}




