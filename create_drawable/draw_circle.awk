# awk -f draw_circle.awk >kreis.svg 
# rsvg-convert kreis.svg -o kreis.png
BEGIN {
  pi = 3.14159265358979323846
  radiand = pi / 180
  r1 = 500
  stroke = 6

  header()

  for (i=0 ; i < 24; i++ ) {
    angle = i*15.0
    if ( angle % 90 == 0 ) {
      r2 = r1 - 60
      stroke = 6
    } else if (angle % 45 == 0) {
      r2 = r1 - 40
      stroke = 5
    } else {
      r2 = r1 - 20
      stroke = 4
    }

    x0 = pointx(0.0+angle,r1)
    y0 = pointy(0.0+angle,r1)
    x1 = pointx(0.0+angle,r2)
    y1 = pointy(0.0+angle,r2)
    printf("<line x1=\"%.4f\" y1=\"%.4f\" x2=\"%.4f\" y2=\"%.4f\" stroke=\"black\" stroke-width=\"%d\"/>\n",x0,y0,x1,y1,stroke)
  }

  footer()

}

func pointx (angle,radius) {
  return 500 - radius * cos(angle * radiand)
}

func pointy (angle,radius) {
  return 500 - radius * sin(angle * radiand)
}

func header () {
  printf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
  printf("<svg\n")
  printf("viewBox=\"-6 -6 1012 1012\" version=\"1.1\"\n")
  printf("xmlns=\"http://www.w3.org/2000/svg\">\n")
  printf("<circle\n")
  printf("style=\"fill:#ffeeaa;stroke:#000000;stroke-width:4;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1\"\n")
  printf("cx=\"500\"\n")
  printf("cy=\"500\"\n")
  printf("r=\"500\"/>\n")
}

func footer () {
  printf("</svg>\n")
}

