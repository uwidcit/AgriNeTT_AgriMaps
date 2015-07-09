# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0018_auto_20150501_1028'),
    ]

    operations = [
        migrations.AlterField(
            model_name='soilsandrainfall',
            name='domsoil',
            field=models.ForeignKey(to='agriApp.soilFeatures', to_field=b'domsoil', null=True),
            preserve_default=True,
        ),
    ]
