package weka.android.processor.test;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.android.annotation.Instance;
import weka.android.annotation.NominalAttribute;
import weka.android.annotation.NumericAttribute;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com> on 2017. 6. 11.
 */

public class InstanceAnnotationTest {
    Instances employeesManual;
    Instances employeesGenerated;

    private List<Employee> getEmployeeList() {
        int id = 1;

        return Arrays.asList(
                new Employee(id++, 180, 85, Gender.MALE),
                new Employee(id++, 170, 90, Gender.MALE),
                new Employee(id++, 176, 70, Gender.MALE),
                new Employee(id++, 179, 100, Gender.MALE),

                new Employee(id++, 150, 40, Gender.FEMALE),
                new Employee(id++, 168, 65, Gender.FEMALE),
                new Employee(id++, 157, 53, Gender.FEMALE),
                new Employee(id++, 175, 60, Gender.FEMALE)
        );
    }

    @Before
    public void setUpManual() {
        // init nominal variable's domain
        List<String> genders = new ArrayList<>();
        for (Gender gender : Gender.values()) {
            genders.add(gender.name());
        }

        // init attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("height"));
        attributes.add(new Attribute("weight"));
        attributes.add(new Attribute("gender", genders));

        // init instances
        List<Employee> employees = getEmployeeList();
        employeesManual = new Instances("employee", attributes, employees.size());

        // add instance
        List<weka.core.Instance> instances = new ArrayList<>();
        for (Employee employee : employees) {
            weka.core.Instance instance = new DenseInstance(attributes.size());
            instance.setDataset(employeesManual);

            instance.setValue(0, employee.height);
            instance.setValue(1, employee.weight);
            instance.setValue(2, employee.gender.name());

            instances.add(instance);
        }
        employeesManual.addAll(instances);
    }

    @Test
    public void testManual() {
        assertThat(employeesManual.size()).isEqualTo(getEmployeeList().size());
    }

    enum Gender { MALE, FEMALE, ALIEN }

    @Instance("employee")
    class Employee {

        int ID;

        @NumericAttribute
        int height;

        @NumericAttribute
        int weight;

        @NominalAttribute({ "MALE", "FEMALE", "ALIEN" })
        Gender gender;

        public Employee(int ID, int height, int weight, Gender gender) {
            this.ID = ID;
            this.height = height;
            this.weight = weight;
            this.gender = gender;
        }
    }
}
