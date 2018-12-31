package com.infoshareacademy.service;

import com.infoshareacademy.dao.ComputerDao;
import com.infoshareacademy.dao.StudentDao;
import com.infoshareacademy.model.Computer;
import com.infoshareacademy.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.print.attribute.standard.Media;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api")
@Transactional
public class RestService {

    private Logger LOG = LoggerFactory.getLogger(RestService.class);

    @Inject
    private StudentDao studentDao;

    @Inject
    private ComputerDao computerDao;


    public RestService() {
    }


    @GET
    @Path("/students")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStudents(){

        List<Student> list = studentDao.findAll();

        LOG.info("Found {} students.", list.size());

        StringBuilder result = new StringBuilder();

        for (Student s : list) {
            result.append(s.toString()).append("\n");
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/students/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStudentByName(@PathParam("name") String name){

        List<Student> list = studentDao.findByName(name);

        LOG.info("Found {} students.", list.size());

        StringBuilder result = new StringBuilder();

        for (Student s: list) {
            result.append(s.toString()).append("\n");
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/computers")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getComputers(){

        List<Computer> list = computerDao.findAll();

        LOG.info("Found {} computers.", list.size());

        StringBuilder result = new StringBuilder();

        for (Computer c : list) {
            result.append(c.toString()).append("\n");
        }
        return Response.ok(result).build();
    }

    @POST
    @Path("/computer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addComputer(Computer computer) {

        Computer newComputer = new Computer(computer.getName(),
                computer.getOperatingSystem());

        LOG.info("Save new computer");
        computerDao.save(newComputer);

        return getComputers();
    }

    @DELETE
    @Path("/computers/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteComputerById(@PathParam("id") String id) {

        if (computerDao.findById(Long.valueOf(id)) == null) {

            LOG.info("Not found computer wih id: {}", id);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        LOG.info("Delete computer with id: {}", id);
        computerDao.delete(Long.valueOf(id));
        return Response.status(200).build();
    }

}

