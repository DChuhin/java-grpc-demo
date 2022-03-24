import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import com.google.protobuf.UInt32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class EncodingsDemoClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        var client = UserServiceGrpc.newBlockingStub(channel);
        var example = UserProto.EncodingsDemoParams.newBuilder()
            .setStringExample("some plain text, present as utf-8")
            .setFloatNumber(3.14f)
            .setVarint(34)
            .setFixedLengthNumber(241251251)
            .build();
        var res = client.encodingsDemo(example);
        channel.shutdown();
    }

}
