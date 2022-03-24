import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import com.google.protobuf.UInt32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClientStreamExample {

    private static final Logger logger = Logger.getLogger(ClientStreamExample.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        var client = UserServiceGrpc.newStub(channel);
        var countDownLatch = new CountDownLatch(1);

        var responseObserver = new StreamObserver<UserProto.AddUsersResponse>() {
            @Override
            public void onNext(UserProto.AddUsersResponse value) {
                String ids = value.getUsersList().stream()
                    .map(UInt32Value::getValue)
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));
                logger.info("got response " + ids);
            }

            @Override
            public void onError(Throwable t) {
                logger.info("error");
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("completed");
                countDownLatch.countDown();
            }
        };

        var requestObserver = client.addUsers(responseObserver);

        var user1 = UserProto.User.newBuilder()
            .setId(21)
            .setName("clientStream1")
            .build();
        var user2 = UserProto.User.newBuilder()
            .setId(22)
            .setName("clientStream2")
            .build();

        requestObserver.onNext(user1);
        logger.info("Sent user " + user1.getName());
        Thread.sleep(3000);
        requestObserver.onNext(user2);
        logger.info("Sent user " + user2.getName());
        Thread.sleep(3000);
        requestObserver.onCompleted();

        countDownLatch.await();
    }
}
