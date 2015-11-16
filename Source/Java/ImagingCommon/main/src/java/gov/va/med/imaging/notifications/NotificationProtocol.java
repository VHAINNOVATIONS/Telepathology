/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.va.med.imaging.notifications;

/**
 *
 * @author Jon Louthian
 */
public interface NotificationProtocol {
    void send(Notification notification);
}
