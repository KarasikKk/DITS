//package com.example.dits.DAO;
//
//import org.hibernate.SessionFactory;
//
//import java.util.List;
//
//public interface DaoRepos<T>{
//    SessionFactory getSessionFactory();
//
//    default void create(T t, SessionFactory sessionFactory){
//        sessionFactory.getCurrentSession().saveOrUpdate(t);
//    }
//    default void update(T t, SessionFactory sessionFactory){
//        sessionFactory.getCurrentSession().update(t);
//    }
//    default void delete(T t, SessionFactory sessionFactory){
//        sessionFactory.getCurrentSession().delete(t);
//    }
//    default void save(T t, SessionFactory sessionFactory){
//        sessionFactory.getCurrentSession().save(t);
//    }
//    default List<T> findAll(Class T, SessionFactory sessionFactory) {
//        List<T> tList = (List<T>)sessionFactory.openSession()
//                .createQuery("From " + T.getSimpleName()).list();
//        return tList;
//    }
//    default void testingCreateMethod(T t, SessionFactory sessionFactory){
//        sessionFactory.getCurrentSession().merge(t);
//    }
//}
