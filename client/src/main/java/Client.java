import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import com.google.protobuf.UInt32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Client {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        var client = UserServiceGrpc.newBlockingStub(channel);
        var user = UserProto.User.newBuilder()
                .setId(123)
                .setName("test")
                .build();
        var res = client.addUser(user);
        System.out.println("res " + res);
        var res2 = client.getUser(UInt32Value.of(123));
        System.out.println("res2 " + res2);
        channel.shutdown();
    }
}
