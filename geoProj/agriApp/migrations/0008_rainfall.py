# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0007_auto_20150202_1842'),
    ]

    operations = [
        migrations.CreateModel(
            name='Rainfall',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('ranges', models.IntegerField()),
                ('rain_range', models.CharField(max_length=30)),
                ('class_r', models.IntegerField()),
                ('geom', django.contrib.gis.db.models.fields.PolygonField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
