import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.logging.Logger;

public class ServerStreamExample {

    private static final Logger logger = Logger.getLogger(ServerStreamExample.class.getName());

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        var client = UserServiceGrpc.newBlockingStub(channel);

        var responseIterator = client.getUsers(UserProto.GetUsersParams.newBuilder().build());

        while (responseIterator.hasNext()) {
            var user = responseIterator.next();
            logger.info(user.toString());
        }
    }
}
