package day04;

/**
 * @author chenxiaonuo
 * @date 2019-08-12 14:05
 */
public class Person {

    private Integer id;
    private String personName;

    public Person() {
    }

    public Person(String personName) {
        this.personName = personName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
