package com.beom.api.totp.demo.dal.entity;

import com.beom.api.totp.demo.dal.MFAType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class that represents the MFA information
 *
 * @author beom
 * @since 2024/03/16
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mfa_info")
public class MfaInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "mfa_type")
    private MFAType mfaType;

    @Column(name = "secret_key")
    private String secretKey;

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key column in MfaInfo table
    private User user;

    // Avoid circular reference in toString() method
    @Override
    public String toString() {
        return "MfaInfo (" +
                "id=" + id +
                ", mfaType=" + mfaType +
                ", secretKey='" + secretKey + '\'' +
                ')';
    }
}
