
package com.queueup.service;

import com.queueup.QueueUpApplication;
import com.queueup.dto.response.JoinQueueResponse;
import com.queueup.dto.response.QueueCreatedResponse;
import com.queueup.dto.response.QueueStateResponse;
import com.queueup.dto.response.ServeNextResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
        import static org.wildfly.common.Assert.assertNotNull;

@SpringBootTest(classes = QueueUpApplication.class)
public class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Test
    public void testFullQueueLifecycle() {
        // 1. Create queue
        QueueCreatedResponse created = queueService.createQueue();
        assertNotNull(created.getCode());
        System.out.println("Created queue: " + created.getCode());

        String code = created.getCode();

        // 2. Join customers
        JoinQueueResponse customer1 = queueService.joinQueue(code, "Alice");
        JoinQueueResponse customer2 = queueService.joinQueue(code, "Bob");
        JoinQueueResponse customer3 = queueService.joinQueue(code, "Charlie");

        assertEquals(1, customer1.getPosition());
        assertEquals(2, customer2.getPosition());
        assertEquals(3, customer3.getPosition());
        System.out.println("3 customers joined");

        // 3. Check queue state
        QueueStateResponse state = queueService.getQueueState(code);
        assertEquals(3, state.getTotalWaiting());
        System.out.println("Queue has " + state.getTotalWaiting() + " waiting");

        // 4. Serve first customer
        ServeNextResponse served = queueService.serveNext(code);
        assertEquals("Alice", served.getName());
        System.out.println("Served: " + served.getName());

        // 5. Check positions recalculated
        state = queueService.getQueueState(code);
        assertEquals(2, state.getTotalWaiting());
        System.out.println("Now " + state.getTotalWaiting() + " waiting");

        // 6. Pause and resume
        queueService.pauseQueue(code);
        state = queueService.getQueueState(code);
        assertEquals("PAUSED", state.getStatus().name());
        System.out.println("Queue paused");

        queueService.resumeQueue(code);
        state = queueService.getQueueState(code);
        assertEquals("OPEN", state.getStatus().name());
        System.out.println("Queue resumed");

        // 7. Close queue
        queueService.closeQueue(code);
        state = queueService.getQueueState(code);
        assertEquals("CLOSED", state.getStatus().name());
        assertEquals(0, state.getTotalWaiting());
        System.out.println("Queue closed, all remaining tickets cancelled");
    }
}