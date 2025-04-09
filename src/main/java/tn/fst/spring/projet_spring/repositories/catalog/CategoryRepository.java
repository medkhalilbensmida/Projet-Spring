package tn.fst.spring.projet_spring.repositories.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.fst.spring.projet_spring.entities.catalog.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {};