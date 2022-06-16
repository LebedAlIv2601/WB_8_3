package com.example.wb_8_3.domain.repository

import com.example.wb_8_3.domain.model.CatModelDomain

interface GetCatRepository {

    suspend fun getCat(): CatModelDomain

}