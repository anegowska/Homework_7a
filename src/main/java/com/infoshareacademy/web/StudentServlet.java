package com.infoshareacademy.web;

import com.infoshareacademy.dao.AddressDao;
import com.infoshareacademy.dao.ComputerDao;
import com.infoshareacademy.dao.CourseDao;
import com.infoshareacademy.dao.StudentDao;
import com.infoshareacademy.model.Address;
import com.infoshareacademy.model.Computer;
import com.infoshareacademy.model.Course;
import com.infoshareacademy.model.Student;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
@WebServlet(urlPatterns = "/student")
public class StudentServlet extends HttpServlet {

    private Logger LOG = LoggerFactory.getLogger(StudentServlet.class);

    @Inject
    private StudentDao studentDao;

    @Inject
    private ComputerDao computerDao;

    @Inject
    private AddressDao addressDao;

    @Inject
    private CourseDao courseDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Test data

        // Courses
        Course course1 = new Course("JJDD5");
        courseDao.save(course1);

        Course course2 = new Course("JJDZ5");
        courseDao.save(course2);

        Course course3 = new Course("JJDDL2");
        courseDao.save(course3);

        // Addresses
        Address a1 = new Address("Grunwaldzka 472B", "Gdansk");
        addressDao.save(a1);

        Address a2 = new Address("Morska 1", "Gdynia");
        addressDao.save(a2);

        // Computers
        Computer c1 = new Computer("DELL Latitude 1234",
            "Ubuntu");
        c1.getId(); // tu id = null
        computerDao.save(c1);
        c1.getId(); // tu juz wartosc jest

        Computer c2 = new Computer("HP Pavillion 321",
            "Windows 10");
        computerDao.save(c2);

        // Students
        Student student1 = new Student("Michal",
            "Malinowski",
            LocalDate.of(2000, 2, 13),
            c1,
            a1,
            Arrays.asList(course1, course2));
        studentDao.save(student1);

        Student student2 = new Student("Marek",
            "Kowalski",
            LocalDate.parse("2001-11-12"),
            c2,
            a1,
            Arrays.asList(course3));
        studentDao.save(student2);

        LOG.info("System time zone is: {}", ZoneId.systemDefault());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        final String action = req.getParameter("action");
        LOG.info("Requested action: {}", action);
        if (action == null || action.isEmpty()) {
            resp.getWriter().write("Empty action parameter.");
            return;
        }

        if (action.equals("findAll")) {
            findAll(req, resp);
        } else if (action.equals("add")) {
            addStudent(req, resp);
        } else if (action.equals("delete")) {
            deleteStudent(req, resp);
        } else if (action.equals("update")) {
            updateStudent(req, resp);
        } else if (action.equals("findByDate")) {
            findByDate(req, resp);
        } else {
            resp.getWriter().write("Unknown action.");
        }
    }

    private void findByDate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LocalDate date = LocalDate.parse(req.getParameter("date"));

        List<Student> result = studentDao.findAllBornAfter(date);

        for (Student p : result) {
            resp.getWriter().write(p.toString() + "\n");
        }
    }

    private void updateStudent(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Updating Student with id = {}", id);

        final Student existingStudent = studentDao.findById(id);
        if (existingStudent == null) {
            LOG.info("No Student found for id = {}, nothing to be updated", id);
        } else {
            existingStudent.setName(req.getParameter("name"));
            existingStudent.setSurname(req.getParameter("surname"));

            String dateStr = req.getParameter("date");
            LocalDate date = LocalDate.parse(dateStr); // YYYY-MM-DD
            existingStudent.setDateOfBirth(date);

            String addressIdStr = req.getParameter("aid");
            Long addressId = Long.valueOf(addressIdStr);
            Address address = addressDao.findById(addressId);
            existingStudent.setAddress(address);

            String courseIdStr = req.getParameter("courseid");
            Long courseId = Long.valueOf(courseIdStr);
            Course course = courseDao.findById(courseId);

            if (existingStudent.getCourses() != null) {
                existingStudent.getCourses().add(course);
            } else {
                existingStudent.setCourses(Arrays.asList(course));
            }

            studentDao.update(existingStudent);
            LOG.info("Student object updated: {}", existingStudent);
        }

        // Return all persisted objects
        findAll(req, resp);
    }

    private void addStudent(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        final Student p = new Student();
        p.setName(req.getParameter("name"));
        p.setSurname(req.getParameter("surname"));

        String dateStr = req.getParameter("date");
        LocalDate date = LocalDate.parse(dateStr); // YYYY-MM-DD
        p.setDateOfBirth(date);

        String computerIdStr = req.getParameter("cid");
        Long computerId = Long.valueOf(computerIdStr);
        Computer c = computerDao.findById(computerId);
        p.setComputer(c);

        String addressIdStr = req.getParameter("aid");
        Long addressId = Long.valueOf(addressIdStr);
        Address address = addressDao.findById(addressId);
        p.setAddress(address);

        String courseIdStr = req.getParameter("courseid");
        Long courseId = Long.valueOf(courseIdStr);
        Course course = courseDao.findById(courseId);

        p.setCourses(Arrays.asList(course));

        studentDao.save(p);
        LOG.info("Saved a new Student object: {}", p);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Long id = Long.parseLong(req.getParameter("id"));
        LOG.info("Removing Student with id = {}", id);

        studentDao.delete(id);

        // Return all persisted objects
        findAll(req, resp);
    }

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final List<Student> result = studentDao.findAll();
        LOG.info("Found {} objects", result.size());
        for (Student p : result) {
            resp.getWriter().write(p.toString() + "\n");
        }
    }
}

