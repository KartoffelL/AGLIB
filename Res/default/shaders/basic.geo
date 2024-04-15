#version 330 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in Data {
	vec2 tex_coords;
	flat float mat;
	vec3 cam_vec;
	vec3 fragposition;
	vec3 cam_pos;

	vec3 normalR;
	mat3 tbn;
	vec4 debug;
} vin[];

out Data {
	vec2 tex_coords;
	flat float mat;
	vec3 cam_vec;
	vec3 fragposition;
	vec3 cam_pos;

	vec3 normalR;
	mat3 tbn;
	vec4 debug;
} vout;


out float vertex;

uniform bool normalMapping;

void main() {
  for(int i = 0; i < 3; i++) { // You used triangles, so it's always 3
    	gl_Position = gl_in[i].gl_Position;
    	gl_ClipDistance[0] = gl_in[i].gl_ClipDistance[0];
    	gl_ClipDistance[1] = gl_in[i].gl_ClipDistance[1];
    	gl_ClipDistance[2] = gl_in[i].gl_ClipDistance[2];
    	gl_ClipDistance[3] = gl_in[i].gl_ClipDistance[3];
      	vertex = i;
      	vout.tex_coords = vin[i].tex_coords;
      	vout.mat = vin[i].mat;
      	vout.cam_vec = vin[i].cam_vec;
      	vout.fragposition = vin[i].fragposition;
      	vout.cam_pos = vin[i].cam_pos;
      	
      	vout.normalR = vin[i].normalR;
      	if(normalMapping) {
	      	//Calculating TBN matrix
	      	int idf = 1;
	      	int ids = 2;
	      	vec3 edge1 = vin[idf].fragposition - vin[0].fragposition;
	        vec3 edge2 = vin[ids].fragposition - vin[0].fragposition;
	        vec2 deltaTexCoord1 = vin[idf].tex_coords - vin[0].tex_coords;
	        vec2 deltaTexCoord2 = vin[ids].tex_coords - vin[0].tex_coords;
	        
	        float f = 1/(deltaTexCoord1.x * deltaTexCoord2.y - deltaTexCoord2.x * deltaTexCoord1.y);
	        
	        vec3 tangent = vec3(0);
			tangent.x = f*(deltaTexCoord2.y * edge1.x - deltaTexCoord1.y * edge2.x); //Bitangent just with deltaTexCoord->x
			tangent.y = f*(deltaTexCoord2.y * edge1.y - deltaTexCoord1.y * edge2.y);
			tangent.z = f*(deltaTexCoord2.y * edge1.z - deltaTexCoord1.y * edge2.z);
			
			
			tangent = normalize(tangent - dot(tangent, vin[i].normalR) * vin[i].normalR);
			vec3 bitangent = cross(vin[i].normalR, tangent);
	        vout.tbn = mat3(tangent, bitangent, vin[i].normalR);
        }
      	vout.debug = vin[i].debug;;
      	
    EmitVertex();
  }
  EndPrimitive();
}