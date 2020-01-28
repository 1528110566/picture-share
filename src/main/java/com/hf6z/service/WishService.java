package com.hf6z.service;

import com.hf6z.model.Wish;

import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-20 23:26
 */
public interface WishService {
    void addWish(Wish wish);

    void saveToDB(List<Wish> wishes);

    List<Wish> getWish();
}
