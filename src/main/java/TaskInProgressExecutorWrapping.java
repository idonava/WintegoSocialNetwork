import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskInProgressExecutorWrapping {
    private final AtomicInteger taskInProcessAtomicInteger = new AtomicInteger(0);
    private final ExecutorService executorService;

    public TaskInProgressExecutorWrapping(ExecutorService executorService) {
        this.executorService = executorService;
    }

    // Execute runnable and increment by one
    public void submit(final Runnable runnable) {
        taskInProcessAtomicInteger.incrementAndGet();
        executorService.submit(new Runnable() {
            public void run() {
                runnable.run();
                taskInProcessAtomicInteger.decrementAndGet();
            }
        });
    }

    public boolean hasTasksInProgress() {
        return taskInProcessAtomicInteger.get() > 0;
    }
}
