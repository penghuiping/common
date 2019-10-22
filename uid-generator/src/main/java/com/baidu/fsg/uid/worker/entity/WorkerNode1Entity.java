package com.baidu.fsg.uid.worker.entity;

import com.php25.common.db.cnd.DbSchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2019/10/21 14:07
 */
@Table("worker_node")
public class WorkerNode1Entity {
    /**
     * Entity unique id (table unique)
     */
    @Id
    @SequenceGenerator(name = "seq_worker_node_id", sequenceName = "seq_worker_node_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Type of CONTAINER: HostName, ACTUAL : IP.
     */
    @Column("host_name")
    private String hostName;

    /**
     * Type of CONTAINER: Port, ACTUAL : Timestamp + Random(0-10000)
     */
    @Column("port")
    private String port;


    @Column("type")
    private int type;

    /**
     * Worker launch date, default now
     */
    @Column("launch_date")
    private LocalDateTime launchDate = LocalDateTime.now();

    /**
     * Created time
     */
    @Column("created")
    private LocalDateTime created;

    /**
     * Last modified
     */
    @Column("modified")
    private LocalDateTime modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDateTime getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDateTime launchDate) {
        this.launchDate = launchDate;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
}
