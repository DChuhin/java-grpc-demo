import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;
import java.util.logging.Logger;

public class Service {

    private static final Logger logger = Logger.getLogger(Service.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        var server = ServerBuilder.forPort(port)
            .addService(new UserServiceImpl())
            .addService(ProtoReflectionService.newInstance())
            .build()
            .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("*** server shut down");
        }));
        server.awaitTermination();
    }
}
