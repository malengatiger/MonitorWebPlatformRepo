/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.monitor.dto;

import com.boha.monitor.data.City;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class CityDTO implements Serializable {

    private Double latitude;
    private Double longitude;
    private static final long serialVersionUID = 1L;
    private Integer cityID;
    private String cityName;
    private List<TownshipDTO> townshipList;
    private Integer provinceID;

    public CityDTO() {
    }

    public CityDTO(Integer cityID) {
        this.cityID = cityID;
    }

    public CityDTO(City a) {
        this.cityID = a.getCityID();
        this.cityName = a.getCityName();
        this.latitude = a.getLatitude();
        this.longitude = a.getLongitude();
        provinceID = a.getProvince().getProvinceID();
    }

    public Integer getCityID() {
        return cityID;
    }

    public void setCityID(Integer cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<TownshipDTO> getTownshipList() {
        return townshipList;
    }

    public void setTownshipList(List<TownshipDTO> townshipList) {
        this.townshipList = townshipList;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }

 

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}