import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class BidirectionalStreamExample {

    private static final Logger logger = Logger.getLogger(BidirectionalStreamExample.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        var client = UserServiceGrpc.newStub(channel);
        var countDownLatch = new CountDownLatch(1);

        var responseObserver = new StreamObserver<UserProto.FindUsersResponse>() {
            @Override
            public void onNext(UserProto.FindUsersResponse value) {
                logger.info("found users: " + value.getUsersList());
            }

            @Override
            public void onError(Throwable t) {
                logger.info("response error");
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("response completed");
                countDownLatch.countDown();
            }
        };

        var requestObserver = client.findUsers(responseObserver);

        requestObserver.onNext(
            UserProto.FindUsersParams.newBuilder()
                .setNameStartsWith("clientStream1")
                .build()
        );
        Thread.sleep(3000);
        requestObserver.onNext(
            UserProto.FindUsersParams.newBuilder()
                .setNameStartsWith("clientStream2")
                .build()
        );
        Thread.sleep(3000);
        requestObserver.onNext(
            UserProto.FindUsersParams.newBuilder()
                .setNameStartsWith("clientStream")
                .build()
        );
        Thread.sleep(3000);
        responseObserver.onCompleted();
        countDownLatch.await();
    }
}
