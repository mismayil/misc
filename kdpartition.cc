#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

struct Point {
   int x;
   int y; 
};

vector<Point> xpoints;  //sorted by x-coordinate
vector<Point> ypoints;  //sorted by y-coordinate

//compare by x-coordinate
int comparex(Point p1, Point p2) {
   return (p1.x < p2.x);
}

//compare by y coordinate
int comparey(Point p1, Point p2) {
   return (p1.y < p2.y);
}

void make_kd(vector<Point> xpoints, vector<Point> ypoints, int depth) {
   
   if (xpoints.size() == 0 && ypoints.size() == 0) return;
   
   vector<Point> spoints;
   vector<Point> tpoints; 
   int mid;

   if (depth % 2 == 0) {
      
      mid = xpoints.size() / 2;
      depth++;

      cout << xpoints[mid].x << " " << xpoints[mid].y << endl;

      for(int i = 0; i < mid; i++) {
      	spoints.push_back(xpoints[i]);
      }

      for (int i = 0; i < ypoints.size(); i++) {
         if (ypoints[i].x < xpoints[mid].x) tpoints.push_back(ypoints[i]);
      }

      make_kd(spoints, tpoints, depth);

      spoints.clear();
      tpoints.clear();

      for(int i = mid+1; i < xpoints.size(); i++) {
      	spoints.push_back(xpoints[i]);
      }

      for (int i = 0; i < ypoints.size(); i++) {
         if (ypoints[i].x > xpoints[mid].x) tpoints.push_back(ypoints[i]);
      }

      make_kd(spoints, tpoints, depth);
      
   } else {
     	
      mid = ypoints.size() / 2;
      depth++;

      cout << ypoints[mid].x << " " << ypoints[mid].y << endl;

      for(int i = 0; i < mid; i++) {
      	spoints.push_back(ypoints[i]);
      }

      for (int i = 0; i < xpoints.size(); i++) {
         if (xpoints[i].y < ypoints[mid].y) tpoints.push_back(xpoints[i]);
      }

      make_kd(spoints, tpoints, depth);

      spoints.clear();
      tpoints.clear();

      for(int i = mid+1; i < ypoints.size(); i++) {
      	spoints.push_back(ypoints[i]);
      }

      for (int i = 0; i < xpoints.size(); i++) {
         if (xpoints[i].y > ypoints[mid].y) 
         	tpoints.push_back(xpoints[i]);
      }

      make_kd(spoints, tpoints, depth);
   }
}

int main() {
   
   int num;
   int x, y;
   cin >> num;
   vector<Point> points;
   
   while (num != 0) {
   
      cin >> x >> y;
      Point p = {x, y};
      points.push_back(p);
      num--;
   }
   
   sort(points.begin(), points.end(), comparex); //sort by x-coordinate
   xpoints = points;
   sort(points.begin(), points.end(), comparey); //sort by y-coordinate
   ypoints = points;
   make_kd(xpoints, ypoints, 0);
   return 0; 
}
