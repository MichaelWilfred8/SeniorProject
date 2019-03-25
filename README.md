# SeniorProject
Using a Blockchain to authenticate Connected Vehicles

Steps:
1. The Startup script on the OBU, sends its Certificate to the SDK using SCP. 
2. The SDK then sends that Certificate to the Host.
3. The Host compares the Certificate to the first Blockchain, which is the Revoked Certificates Chain. 
4. If it is found in that chain, it sends the ShutDown script to the OBU to prevent it from sending messages to other vehicles.
5. If it is not in that chain, it compares it to the second Blockchain, which is the Issued Certificates Chain.
6. If it is found, it sends a message to the SDK saying "Authenticated" which does not stop the OBU from sending messages.
7. If it is not found in either chain, that means the Certificate was not Issued by a trusted entity. The Host sends the ShutDown script to the OBU to prevent it from sending messages to other vehicles.

Roles:
Host - The computer that was running the Blockchain algorithm using a local SQL Database
SDK - The computer the Vehicle was directly connected to
OBU - The Onboard Unit, a Cohda MK5 

The schematic.png shows an overview of how it works. The RSU, or Road-side Unit is represented by the
Host. The car is represented by both the SDK and OBU. 
