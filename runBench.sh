#!/bin/bash
exec ./som -w --mate -activateMate -A -cp \
    Smalltalk:Smalltalk/Mate:Smalltalk/Mate/MOP:Smalltalk/Mate/Compiler:Examples:Examples/Benchmarks:Examples/Benchmarks/Mate/IndividualOperations:Examples/Benchmarks/Mate/Tracing:Examples/Benchmarks/NBody:Examples/Benchmarks/Json:Examples/Benchmarks/CD:Examples/Benchmarks/DeltaBlue:Examples/Benchmarks/Havlak:Examples/Benchmarks/Mate/Immutability:Examples/Benchmarks/Mate/Immutability/DelegationProxies:Examples/Benchmarks/Mate/Immutability/Handles \
    BenchmarkHarness $@
