FROM envoyproxy/envoy-dev:0b769f6e299dac9115cba10008ce984d0ebf951a

COPY envoy.yaml /etc/envoy/envoy.yaml
COPY UserProto.pb /UserProto.pb

RUN chmod go+r /etc/envoy/envoy.yaml
