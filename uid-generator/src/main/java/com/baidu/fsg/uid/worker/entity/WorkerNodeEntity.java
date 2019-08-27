/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.fsg.uid.worker.entity;

import com.baidu.fsg.uid.worker.WorkerNodeType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

/**
 * Entity for M_WORKER_NODE
 *
 * @author yutianbao
 */
@Table("WORKER_NODE")
public class WorkerNodeEntity {

    /**
     * Entity unique id (table unique)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Type of CONTAINER: HostName, ACTUAL : IP.
     */
    @Column("HOST_NAME")
    private String hostName;

    /**
     * Type of CONTAINER: Port, ACTUAL : Timestamp + Random(0-10000)
     */
    @Column("PORT")
    private String port;

    /**
     * type of {@link WorkerNodeType}
     */
    @Column("TYPE")
    private int type;

    /**
     * Worker launch date, default now
     */
    @Column("LAUNCH_DATE")
    private Date launchDate = new Date();

    /**
     * Created time
     */
    @Column("CREATED")
    private Date created;

    /**
     * Last modified
     */
    @Column("MODIFIED")
    private Date modified;

    /**
     * Getters & Setters
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDateDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }


}
