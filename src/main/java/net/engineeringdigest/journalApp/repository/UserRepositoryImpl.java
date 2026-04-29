package net.engineeringdigest.journalApp.repository;

import net.engineeringdigest.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl{

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserForSA() {     //need list of users whose email exists and have opted for SA
        Query query = new Query();

//        query.addCriteria(Criteria.where("email").exists(true));
//        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));

        //from these query we will get the users who have an email and has opted for SA
        Criteria criteria = new Criteria();
        query.addCriteria(criteria.andOperator(
                Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"),
                Criteria.where("sentimentAnalysis").is(true)
        ));
//        query.addCriteria(Criteria.where("roles").in("USER", "ADMIN"));

        List<User> users = mongoTemplate.find(query, User.class);//returns all users that have name Rohit
        return users;
    }
}
