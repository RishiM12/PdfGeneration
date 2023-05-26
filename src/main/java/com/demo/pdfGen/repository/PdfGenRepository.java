package com.demo.pdfGen.repository;

import com.demo.pdfGen.model.Customer;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PdfGenRepository extends ReactiveCouchbaseRepository<Customer, String> {
    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND customerNumber=$1")
    Mono<Customer> findByCustomerNumber(String number);
}
